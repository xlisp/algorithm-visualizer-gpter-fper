(ns functional-programming-visualgo-fp.algo.graph
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 图结构可视化
;; ============================================================

(defn parse-edges
  "解析边列表字符串 '0-1,1-2,2-3' 为 [[0 1] [1 2] [2 3]]"
  [edge-str]
  (mapv (fn [e]
          (let [parts (clojure.string/split e #"-")]
            [(js/parseInt (first parts)) (js/parseInt (second parts))]))
        (clojure.string/split edge-str #",")))

(defn get-all-nodes [edges]
  (vec (sort (distinct (flatten edges)))))

(defn graph->dot-frames
  "逐步添加边，生成帧"
  [edges directed?]
  (let [all-nodes (get-all-nodes edges)
        arrow (if directed? " -> " " -- ")
        graph-type (if directed? "digraph" "graph")]
    (vec
      (for [step (range 1 (inc (count edges)))]
        (let [visible-edges (take step edges)
              current-edge (last visible-edges)
              active-nodes (set (flatten (vec current-edge)))]
          (str "
  " graph-type " {
    graph [label=\"图结构 - 步骤 " step "/" (count edges) "\" labelloc=\"t\" fontsize=\"20.0\" tooltip=\" \"]
    node [style=\"filled\"]
"
               (clojure.string/join "\n"
                 (concat
                   (map (fn [n]
                          (let [color (if (active-nodes n) "#d62728" "#1f77b4")]
                            (str "  n" n " [label=\"" n "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                        all-nodes)
                   (map (fn [[a b]]
                          (let [color (if (= [a b] current-edge) "red" "black")]
                            (str "  n" a arrow "n" b " [color=\"" color "\" penwidth=2]")))
                        visible-edges)))
               "\n  }"))))))

(defn run-graph! [edge-str directed?]
  (let [edges (parse-edges edge-str)
        frames (graph->dot-frames edges directed?)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1,0-2,1-3,1-4,2-4,3-5,4-5")
                     directed? (reagent/atom false)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "创建图" :menu-item-name "create" :click-fn nil}]
          left-menu-item-datas
          {"graphviz" [:div]
           "create"
           [:div.flex.flex-column {:style {:margin-top "2.5em"}}
            [:div.flex.flex-row
             [:div.ml1
              [:input {:value @edge-value
                       :on-change #(reset! edge-value (.. % -target -value))
                       :style {:width "12em"}
                       :placeholder "边列表(如0-1,1-2)"}]]]
            [:div.flex.flex-row.mt1
             [:div.bg-yellow.ml1.pa1.f6
              {:on-click #(run-graph! @edge-value false)
               :class (<class css/hover-menu-style)
               :style {:width "5em"}} "无向图"]
             [:div.bg-yellow.ml1.pa1.f6
              {:on-click #(run-graph! @edge-value true)
               :class (<class css/hover-menu-style)
               :style {:width "5em"}} "有向图"]]]}]
      (comps/base-page
        :title "图结构"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

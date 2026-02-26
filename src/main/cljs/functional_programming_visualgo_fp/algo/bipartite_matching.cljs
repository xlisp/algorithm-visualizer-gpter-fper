(ns functional-programming-visualgo-fp.algo.bipartite-matching
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 二分匹配 - 匈牙利算法可视化
;; ============================================================

(defn parse-bipartite-edges [edge-str]
  (mapv (fn [e]
          (let [[u v] (clojure.string/split e #"-")]
            [(js/parseInt u) (js/parseInt v)]))
        (clojure.string/split edge-str #",")))

(defn try-augment
  "尝试为左侧节点 u 找增广路"
  [adj u match-right visited]
  (some (fn [v]
          (when-not (@visited v)
            (swap! visited conj v)
            (when (or (= -1 (get @match-right v -1))
                      (try-augment adj (get @match-right v) match-right visited))
              (swap! match-right assoc v u)
              true)))
        (get adj u [])))

(defn hungarian-frames
  "匈牙利算法逐步执行"
  [edge-str left-n right-n]
  (let [edges (parse-bipartite-edges edge-str)
        adj (reduce (fn [m [u v]] (update m u (fnil conj []) v)) {} edges)
        match-right (atom {})
        frames (atom [])
        left-nodes (range left-n)
        right-nodes (range right-n)]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "二分匹配 - 初始状态"
             (concat
               ["rankdir=LR"
                "subgraph cluster_left { label=\"左\" color=blue"
                (clojure.string/join " "
                  (map #(str "L" % " [label=\"L" % "\" shape=\"circle\" fillcolor=\"#1f77b4\"]") left-nodes))
                "}"
                "subgraph cluster_right { label=\"右\" color=red"
                (clojure.string/join " "
                  (map #(str "R" % " [label=\"R" % "\" shape=\"circle\" fillcolor=\"#ff7f0e\"]") right-nodes))
                "}"]
               (map (fn [[u v]] (str "L" u " -> R" v " [color=gray]")) edges))))
    ;; 逐个左节点匹配
    (doseq [u left-nodes]
      (let [visited (atom #{})
            success (try-augment adj u match-right visited)
            matched-edges (set (map (fn [[v u]] [u v]) @match-right))]
        (swap! frames conj
               (graphviz/dot-template
                 (str "二分匹配 - 为L" u (if success " 找到匹配" " 无法匹配"))
                 (concat
                   ["rankdir=LR"
                    "subgraph cluster_left { label=\"左\" color=blue"
                    (clojure.string/join " "
                      (map (fn [l]
                             (let [color (if (= l u) "#d62728" "#1f77b4")]
                               (str "L" l " [label=\"L" l "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                           left-nodes))
                    "}"
                    "subgraph cluster_right { label=\"右\" color=red"
                    (clojure.string/join " "
                      (map #(str "R" % " [label=\"R" % "\" shape=\"circle\" fillcolor=\"#ff7f0e\"]") right-nodes))
                    "}"]
                   (map (fn [[eu ev]]
                          (let [matched (matched-edges [eu ev])
                                color (if matched "green" "gray")
                                width (if matched "3" "1")]
                            (str "L" eu " -> R" ev " [color=" color " penwidth=" width "]")))
                        edges))))))
    @frames))

(defn run-bipartite! [edge-str left-n right-n]
  (let [frames (hungarian-frames edge-str left-n right-n)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-0,0-1,1-0,1-2,2-1,2-2")
                     left-n (reagent/atom 3)
                     right-n (reagent/atom 3)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "匈牙利算法" :menu-item-name "hungarian" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "匈牙利算法 O(V*E)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "hungarian"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "12em"}
                      :placeholder "边(L-R对)"}]]
            [:div.ml1
             [:input {:value @left-n
                      :on-change #(reset! left-n (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "左"
                      :type "number"}]]
            [:div.ml1
             [:input {:value @right-n
                      :on-change #(reset! right-n (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "右"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-bipartite! @edge-value (js/parseInt @left-n) (js/parseInt @right-n))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "二分匹配 - 匈牙利算法"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

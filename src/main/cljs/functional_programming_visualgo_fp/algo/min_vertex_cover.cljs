(ns functional-programming-visualgo-fp.algo.min-vertex-cover
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 最小顶点覆盖 - 贪心近似算法可视化
;; ============================================================

(defn parse-edges [edge-str]
  (mapv (fn [e]
          (let [[u v] (clojure.string/split e #"-")]
            [(js/parseInt u) (js/parseInt v)]))
        (clojure.string/split edge-str #",")))

(defn vertex-cover-frames
  "贪心近似最小顶点覆盖，每次选择度最大的节点"
  [edge-str]
  (let [edges (parse-edges edge-str)
        all-nodes (vec (sort (distinct (flatten edges))))
        frames (atom [])
        cover (atom #{})
        uncovered (atom (set (range (count edges))))]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "最小顶点覆盖 - 初始状态"
             (concat
               (map (fn [n]
                      (str "n" n " [label=\"" n "\" shape=\"circle\" fillcolor=\"#1f77b4\"]"))
                    all-nodes)
               (map-indexed (fn [i [u v]]
                              (str "n" u " -> n" v " [dir=none color=gray]"))
                            edges))))
    ;; 贪心选择
    (loop [remaining-edges (set (range (count edges)))
           step 0]
      (when (and (seq remaining-edges) (< step 20))
        ;; 计算每个节点的度（仅在未覆盖的边中）
        (let [degrees (reduce (fn [m idx]
                                (let [[u v] (nth edges idx)]
                                  (-> m
                                      (update u (fnil inc 0))
                                      (update v (fnil inc 0)))))
                              {} remaining-edges)
              ;; 选择度最大的节点
              best-node (key (apply max-key val degrees))]
          (swap! cover conj best-node)
          ;; 移除被覆盖的边
          (let [new-remaining (set (remove (fn [i]
                                            (let [[u v] (nth edges i)]
                                              (or (= u best-node) (= v best-node))))
                                          remaining-edges))]
            (swap! frames conj
                   (graphviz/dot-template
                     (str "顶点覆盖 - 选择节点 " best-node " (覆盖集: " @cover ")")
                     (concat
                       (map (fn [n]
                              (let [color (cond
                                            (= n best-node) "#d62728"
                                            (@cover n) "#2ca02c"
                                            :else "#1f77b4")]
                                (str "n" n " [label=\"" n "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                            all-nodes)
                       (map-indexed (fn [i [u v]]
                                      (let [covered (not (new-remaining i))
                                            color (if covered "green" "gray")
                                            width (if covered "2" "1")]
                                        (str "n" u " -> n" v " [dir=none color=" color " penwidth=" width "]")))
                                    edges))))
            (recur new-remaining (inc step))))))
    @frames))

(defn run-vertex-cover! [edge-str]
  (let [frames (vertex-cover-frames edge-str)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1,0-2,1-2,1-3,2-4,3-4,3-5")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "贪心覆盖" :menu-item-name "greedy-cover" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "贪心近似顶点覆盖 O(V+E)，近似比2")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "greedy-cover"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "14em"}
                      :placeholder "边列表"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-vertex-cover! @edge-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "最小顶点覆盖"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

(ns functional-programming-visualgo-fp.algo.mst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 最小生成树 - Kruskal算法可视化
;; ============================================================

(defn parse-weighted-edges
  "解析带权边 '0-1:4,1-2:3' => [{:u 0 :v 1 :w 4} ...]"
  [edge-str]
  (mapv (fn [e]
          (let [[nodes w] (clojure.string/split e #":")
                [u v] (clojure.string/split nodes #"-")]
            {:u (js/parseInt u) :v (js/parseInt v) :w (js/parseInt w)}))
        (clojure.string/split edge-str #",")))

(defn find-root [parent x]
  (if (= (nth parent x) x)
    x
    (recur parent (nth parent x))))

(defn kruskal-frames
  "Kruskal算法逐步执行，生成帧"
  [edge-str]
  (let [edges (sort-by :w (parse-weighted-edges edge-str))
        all-nodes (vec (sort (distinct (mapcat (fn [e] [(:u e) (:v e)]) edges))))
        n (inc (apply max all-nodes))
        frames (atom [])
        parent (atom (vec (range n)))
        mst-edges (atom #{})]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "Kruskal - 初始状态 (边按权重排序)"
             (concat
               (map (fn [nd]
                      (str "n" nd " [label=\"" nd "\" shape=\"circle\" fillcolor=\"#1f77b4\"]"))
                    all-nodes)
               (map (fn [e]
                      (str "n" (:u e) " -> n" (:v e)
                           " [dir=none label=\"" (:w e) "\" color=gray]"))
                    edges))))
    ;; 逐步加边
    (doseq [edge edges]
      (let [ru (find-root @parent (:u edge))
            rv (find-root @parent (:v edge))
            accepted (not= ru rv)]
        (when accepted
          (swap! parent assoc ru rv)
          (swap! mst-edges conj [(:u edge) (:v edge)]))
        (swap! frames conj
               (graphviz/dot-template
                 (str "Kruskal - 检查边(" (:u edge) "," (:v edge) ") w=" (:w edge)
                      (if accepted " [接受]" " [拒绝-成环]"))
                 (concat
                   (map (fn [nd]
                          (let [color (cond
                                        (= nd (:u edge)) "#d62728"
                                        (= nd (:v edge)) "#ff7f0e"
                                        :else "#1f77b4")]
                            (str "n" nd " [label=\"" nd "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                        all-nodes)
                   (map (fn [e]
                          (let [in-mst (@mst-edges [(:u e) (:v e)])
                                is-current (and (= (:u e) (:u edge)) (= (:v e) (:v edge)))
                                color (cond is-current (if accepted "green" "red")
                                            in-mst "green"
                                            :else "gray")
                                width (if (or in-mst is-current) "3" "1")]
                            (str "n" (:u e) " -> n" (:v e)
                                 " [dir=none label=\"" (:w e) "\" color=" color " penwidth=" width "]")))
                        edges))))))
    @frames))

(defn run-mst! [edge-str]
  (let [frames (kruskal-frames edge-str)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1:4,0-2:3,1-2:1,1-3:2,2-3:4,2-4:5,3-4:7")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "Kruskal" :menu-item-name "kruskal" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Kruskal O(E log E)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "kruskal"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "边(如0-1:4,1-2:3)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-mst! @edge-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "最小生成树 - Kruskal"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

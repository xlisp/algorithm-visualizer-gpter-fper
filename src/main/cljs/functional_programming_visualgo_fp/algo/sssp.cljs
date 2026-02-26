(ns functional-programming-visualgo-fp.algo.sssp
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; Dijkstra 单源最短路径可视化
;; ============================================================

(defn parse-weighted-edges [edge-str]
  (mapv (fn [e]
          (let [[nodes w] (clojure.string/split e #":")
                [u v] (clojure.string/split nodes #"-")]
            {:u (js/parseInt u) :v (js/parseInt v) :w (js/parseInt w)}))
        (clojure.string/split edge-str #",")))

(defn build-adj [edges]
  (reduce (fn [adj {:keys [u v w]}]
            (-> adj
                (update u (fnil conj []) {:to v :w w})
                (update v (fnil conj []) {:to u :w w})))
          {} edges))

(defn dijkstra-frames
  "Dijkstra算法逐步执行，生成帧"
  [edge-str start]
  (let [edges (parse-weighted-edges edge-str)
        adj (build-adj edges)
        all-nodes (vec (sort (distinct (mapcat (fn [e] [(:u e) (:v e)]) edges))))
        inf 999999
        frames (atom [])
        dist (atom (into {} (map #(vector % inf) all-nodes)))
        visited (atom #{})
        prev-node (atom {})]
    (swap! dist assoc start 0)
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template (str "Dijkstra - 起点 " start)
             (concat
               (map (fn [n]
                      (let [d (get @dist n inf)
                            color (if (= n start) "#d62728" "#1f77b4")]
                        (str "n" n " [label=\"" n "\\nd=" (if (= d inf) "∞" d)
                             "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                    all-nodes)
               (map (fn [e]
                      (str "n" (:u e) " -> n" (:v e)
                           " [dir=none label=\"" (:w e) "\" color=gray]"))
                    edges))))
    ;; 主循环
    (dotimes [_ (count all-nodes)]
      (let [unvisited (remove @visited all-nodes)
            current (when (seq unvisited)
                      (apply min-key #(get @dist % inf) unvisited))]
        (when (and current (< (get @dist current inf) inf))
          (swap! visited conj current)
          (doseq [{:keys [to w]} (get adj current [])]
            (let [new-dist (+ (get @dist current) w)]
              (when (< new-dist (get @dist to inf))
                (swap! dist assoc to new-dist)
                (swap! prev-node assoc to current))))
          (swap! frames conj
                 (graphviz/dot-template
                   (str "Dijkstra - 访问节点 " current " (d=" (get @dist current) ")")
                   (concat
                     (map (fn [n]
                            (let [d (get @dist n inf)
                                  color (cond
                                          (= n current) "#d62728"
                                          (@visited n) "#2ca02c"
                                          :else "#1f77b4")]
                              (str "n" n " [label=\"" n "\\nd=" (if (= d inf) "∞" d)
                                   "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                          all-nodes)
                     (map (fn [e]
                            (let [on-path (or (and (= (:u e) (get @prev-node (:v e)))
                                                   (@visited (:v e)))
                                              (and (= (:v e) (get @prev-node (:u e)))
                                                   (@visited (:u e))))
                                  color (if on-path "green" "gray")
                                  width (if on-path "3" "1")]
                              (str "n" (:u e) " -> n" (:v e)
                                   " [dir=none label=\"" (:w e) "\" color=" color " penwidth=" width "]")))
                          edges)))))))
    @frames))

(defn run-dijkstra! [edge-str start]
  (let [frames (dijkstra-frames edge-str start)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1:4,0-2:2,1-2:1,1-3:5,2-3:8,2-4:10,3-4:2")
                     start-value (reagent/atom 0)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "Dijkstra" :menu-item-name "dijkstra" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Dijkstra O((V+E)log V)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "dijkstra"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "带权边"}]]
            [:div.ml1
             [:input {:value @start-value
                      :on-change #(reset! start-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "起点"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-dijkstra! @edge-value (js/parseInt @start-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "单源最短路径 - Dijkstra"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

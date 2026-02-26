(ns functional-programming-visualgo-fp.algo.network-flow
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 网络流 - Ford-Fulkerson (BFS增广路) 可视化
;; ============================================================

(defn parse-flow-edges [edge-str]
  (mapv (fn [e]
          (let [[nodes c] (clojure.string/split e #":")
                [u v] (clojure.string/split nodes #"-")]
            {:u (js/parseInt u) :v (js/parseInt v) :cap (js/parseInt c)}))
        (clojure.string/split edge-str #",")))

(defn flow-state->dot [edges flow-map current-path label all-nodes]
  (graphviz/dot-template
    label
    (concat
      (map (fn [n]
             (let [on-path (some #{n} current-path)
                   color (cond
                           on-path "#d62728"
                           :else "#1f77b4")]
               (str "n" n " [label=\"" n "\" shape=\"circle\" fillcolor=\"" color "\"]")))
           all-nodes)
      (map (fn [e]
             (let [key [(:u e) (:v e)]
                   f (get flow-map key 0)
                   on-path (and (seq current-path)
                                (some (fn [[a b]] (and (= a (:u e)) (= b (:v e))))
                                      (partition 2 1 current-path)))
                   color (cond on-path "red"
                               (pos? f) "green"
                               :else "gray")
                   width (if on-path "3" "1")]
               (str "n" (:u e) " -> n" (:v e)
                    " [label=\"" f "/" (:cap e) "\" color=" color " penwidth=" width "]")))
           edges))))

(defn bfs-augment [edges capacity flow-map source sink n]
  (let [parent (atom (vec (repeat n -1)))
        visited (atom #{source})
        queue (atom [source])]
    (reset! parent (assoc (vec (repeat n -1)) source source))
    (loop []
      (when (and (seq @queue) (not (@visited sink)))
        (let [u (first @queue)]
          (swap! queue rest)
          (doseq [v (range n)]
            (let [cap (get capacity [u v] 0)
                  f (get flow-map [u v] 0)]
              (when (and (not (@visited v)) (pos? (- cap f)))
                (swap! visited conj v)
                (swap! parent assoc v u)
                (swap! queue conj v)))))
        (recur)))
    (when (@visited sink)
      ;; 回溯路径
      (loop [v sink path [sink]]
        (if (= v source)
          (vec (reverse path))
          (recur (nth @parent v) (conj path (nth @parent v))))))))

(defn ford-fulkerson-frames [edge-str source sink]
  (let [edges (parse-flow-edges edge-str)
        all-nodes (vec (sort (distinct (mapcat (fn [e] [(:u e) (:v e)]) edges))))
        n (inc (apply max all-nodes))
        capacity (reduce (fn [m e] (assoc m [(:u e) (:v e)] (:cap e))) {} edges)
        flow-map (atom {})
        frames (atom [])
        max-flow (atom 0)]
    ;; 初始帧
    (swap! frames conj
           (flow-state->dot edges @flow-map [] "网络流 - 初始状态" all-nodes))
    ;; 增广循环
    (loop [iter 0]
      (when (< iter 20)
        (if-let [path (bfs-augment edges capacity @flow-map source sink n)]
          (let [;; 找瓶颈
                bottleneck (apply min
                             (map (fn [[u v]]
                                    (- (get capacity [u v] 0) (get @flow-map [u v] 0)))
                                  (partition 2 1 path)))]
            ;; 更新流量
            (doseq [[u v] (partition 2 1 path)]
              (swap! flow-map update [u v] (fnil + 0) bottleneck)
              (swap! flow-map update [v u] (fnil - 0) bottleneck))
            (swap! max-flow + bottleneck)
            (swap! frames conj
                   (flow-state->dot edges @flow-map path
                                    (str "增广路: " path " 瓶颈=" bottleneck " 总流量=" @max-flow)
                                    all-nodes))
            (recur (inc iter)))
          ;; 无增广路
          (swap! frames conj
                 (flow-state->dot edges @flow-map []
                                  (str "网络流完成 - 最大流=" @max-flow)
                                  all-nodes)))))
    @frames))

(defn run-network-flow! [edge-str source sink]
  (let [frames (ford-fulkerson-frames edge-str source sink)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1:10,0-2:8,1-2:5,1-3:7,2-4:10,3-4:6,3-5:10,4-5:8")
                     source-value (reagent/atom 0)
                     sink-value (reagent/atom 5)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "最大流" :menu-item-name "max-flow" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Ford-Fulkerson O(E * max_flow)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "max-flow"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "边(u-v:容量)"}]]
            [:div.ml1
             [:input {:value @source-value
                      :on-change #(reset! source-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "源"
                      :type "number"}]]
            [:div.ml1
             [:input {:value @sink-value
                      :on-change #(reset! sink-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "汇"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-network-flow! @edge-value (js/parseInt @source-value) (js/parseInt @sink-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "网络流 - Ford-Fulkerson"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

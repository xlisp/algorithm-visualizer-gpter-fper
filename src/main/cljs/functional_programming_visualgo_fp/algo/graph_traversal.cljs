(ns functional-programming-visualgo-fp.algo.graph-traversal
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 图遍历（BFS & DFS）可视化
;; ============================================================

(defn parse-edges [edge-str]
  (mapv (fn [e]
          (let [parts (clojure.string/split e #"-")]
            [(js/parseInt (first parts)) (js/parseInt (second parts))]))
        (clojure.string/split edge-str #",")))

(defn build-adj-list [edges]
  (reduce (fn [adj [u v]]
            (-> adj
                (update u (fnil conj []) v)
                (update v (fnil conj []) u)))
          {} edges))

(defn graph-state->dot [edges visited current queue-or-stack label]
  (let [all-nodes (vec (sort (distinct (flatten edges))))]
    (graphviz/dot-template
      label
      (concat
        (map (fn [n]
               (let [color (cond
                             (= n current) "#d62728"
                             (visited n) "#2ca02c"
                             ((set queue-or-stack) n) "#ff7f0e"
                             :else "#1f77b4")]
                 (str "n" n " [label=\"" n "\" shape=\"circle\" fillcolor=\"" color "\"]")))
             all-nodes)
        (map (fn [[u v]]
               (str "n" u " -> n" v " [dir=none]"))
             edges)))))

(defn bfs-frames
  "BFS遍历，生成每一步的帧"
  [edges start]
  (let [adj (build-adj-list edges)
        frames (atom [])
        visited (atom #{})
        queue (atom [start])]
    (swap! frames conj (graph-state->dot edges #{} start [start] (str "BFS - 起点 " start)))
    (while (not (empty? @queue))
      (let [current (first @queue)]
        (swap! queue rest)
        (when-not (@visited current)
          (swap! visited conj current)
          (let [neighbors (sort (filter #(not (@visited %)) (get adj current [])))]
            (swap! queue into neighbors)
            (swap! frames conj
                   (graph-state->dot edges @visited current (vec @queue)
                                     (str "BFS - 访问 " current " 队列:" (vec @queue))))))))
    (swap! frames conj (graph-state->dot edges @visited -1 [] "BFS - 遍历完成"))
    @frames))

(defn dfs-frames
  "DFS遍历，生成每一步的帧"
  [edges start]
  (let [adj (build-adj-list edges)
        frames (atom [])
        visited (atom #{})
        stack (atom [start])]
    (swap! frames conj (graph-state->dot edges #{} start [start] (str "DFS - 起点 " start)))
    (while (not (empty? @stack))
      (let [current (peek @stack)]
        (swap! stack pop)
        (when-not (@visited current)
          (swap! visited conj current)
          (let [neighbors (sort > (filter #(not (@visited %)) (get adj current [])))]
            (swap! stack into neighbors)
            (swap! frames conj
                   (graph-state->dot edges @visited current (vec @stack)
                                     (str "DFS - 访问 " current " 栈:" (vec @stack))))))))
    (swap! frames conj (graph-state->dot edges @visited -1 [] "DFS - 遍历完成"))
    @frames))

(defn run-bfs! [edge-str start]
  (let [edges (parse-edges edge-str)
        frames (bfs-frames edges start)]
    (graphviz/render-list "#graph" frames (atom 0))))

(defn run-dfs! [edge-str start]
  (let [edges (parse-edges edge-str)
        frames (dfs-frames edges start)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1,0-2,1-3,1-4,2-4,3-5,4-5")
                     start-value (reagent/atom 0)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "BFS" :menu-item-name "bfs" :click-fn nil}
           {:button-name "DFS" :menu-item-name "dfs" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "BFS/DFS O(V+E)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "bfs"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "12em"}
                      :placeholder "边列表"}]]
            [:div.ml1
             [:input {:value @start-value
                      :on-change #(reset! start-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "起点"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-bfs! @edge-value (js/parseInt @start-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "BFS"]]
           "dfs"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "12em"}
                      :placeholder "边列表"}]]
            [:div.ml1
             [:input {:value @start-value
                      :on-change #(reset! start-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "起点"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-dfs! @edge-value (js/parseInt @start-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "DFS"]]}]
      (comps/base-page
        :title "图遍历 - BFS/DFS"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

(ns functional-programming-visualgo-fp.algo.tsp
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 旅行商问题 - 暴力/最近邻可视化
;; ============================================================

(defn parse-dist-matrix
  "解析距离矩阵字符串 '0:4:8,4:0:6,8:6:0'"
  [matrix-str]
  (mapv (fn [row]
          (mapv js/parseInt (clojure.string/split row #":")))
        (clojure.string/split matrix-str #",")))

(defn nearest-neighbor-frames
  "最近邻启发式TSP，从节点0开始"
  [dist-matrix]
  (let [n (count dist-matrix)
        frames (atom [])
        visited (atom #{0})
        path (atom [0])
        total-cost (atom 0)]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "TSP最近邻 - 起点0"
             (concat
               (map (fn [i]
                      (str "n" i " [label=\"" i "\" shape=\"circle\" fillcolor=\""
                           (if (= i 0) "#d62728" "#1f77b4") "\"]"))
                    (range n))
               (mapcat (fn [i]
                         (keep (fn [j]
                                 (when (< i j)
                                   (str "n" i " -> n" j " [dir=none label=\""
                                        (nth (nth dist-matrix i) j) "\" color=gray]")))
                               (range n)))
                       (range n)))))
    ;; 逐步选最近
    (dotimes [_ (dec n)]
      (let [current (peek @path)
            next-node (apply min-key
                             (fn [j] (nth (nth dist-matrix current) j))
                             (remove @visited (range n)))]
        (swap! visited conj next-node)
        (swap! total-cost + (nth (nth dist-matrix current) next-node))
        (swap! path conj next-node)
        (swap! frames conj
               (graphviz/dot-template
                 (str "TSP - 从" current "到" next-node
                      " 代价=" (nth (nth dist-matrix current) next-node)
                      " 总代价=" @total-cost)
                 (concat
                   (map (fn [i]
                          (let [color (cond
                                        (= i next-node) "#d62728"
                                        (@visited i) "#2ca02c"
                                        :else "#1f77b4")]
                            (str "n" i " [label=\"" i "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                        (range n))
                   ;; 路径边
                   (map (fn [k]
                          (str "n" (nth @path k) " -> n" (nth @path (inc k))
                               " [color=green penwidth=3 label=\""
                               (nth (nth dist-matrix (nth @path k)) (nth @path (inc k))) "\"]"))
                        (range (dec (count @path))))
                   ;; 其他边
                   (mapcat (fn [i]
                             (keep (fn [j]
                                     (when (and (< i j)
                                                (not (some (fn [k]
                                                             (let [a (nth @path k)
                                                                   b (nth @path (inc k))]
                                                               (or (and (= a i) (= b j))
                                                                   (and (= a j) (= b i)))))
                                                           (range (dec (count @path))))))
                                       (str "n" i " -> n" j " [dir=none label=\""
                                            (nth (nth dist-matrix i) j) "\" color=gray]")))
                                   (range n)))
                           (range n)))))))
    ;; 返回起点
    (let [last-node (peek @path)]
      (swap! total-cost + (nth (nth dist-matrix last-node) 0))
      (swap! path conj 0)
      (swap! frames conj
             (graphviz/dot-template
               (str "TSP完成 - 总代价=" @total-cost " 路径:" @path)
               (concat
                 (map (fn [i]
                        (str "n" i " [label=\"" i "\" shape=\"circle\" fillcolor=\"#2ca02c\"]"))
                      (range n))
                 (map (fn [k]
                        (str "n" (nth @path k) " -> n" (nth @path (inc k))
                             " [color=green penwidth=3 label=\""
                             (nth (nth dist-matrix (nth @path k)) (nth @path (inc k))) "\"]"))
                      (range (dec (count @path))))))))
    @frames))

(defn run-tsp! [matrix-str]
  (let [dist (parse-dist-matrix matrix-str)
        frames (nearest-neighbor-frames dist)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [matrix-value (reagent/atom "0:10:15:20,10:0:35:25,15:35:0:30,20:25:30:0")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "最近邻" :menu-item-name "nearest" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "TSP暴力O(n!)，最近邻O(n^2)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "nearest"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @matrix-value
                      :on-change #(reset! matrix-value (.. % -target -value))
                      :style {:width "20em"}
                      :placeholder "距离矩阵(行用,分隔,列用:分隔)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-tsp! @matrix-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "旅行商问题 (TSP)"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

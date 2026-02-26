(ns functional-programming-visualgo-fp.algo.steiner-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; Steiner Tree - 近似算法可视化
;; 使用MST近似：在终端节点上求MST
;; ============================================================

(defn parse-weighted-edges [edge-str]
  (mapv (fn [e]
          (let [[nodes w] (clojure.string/split e #":")
                [u v] (clojure.string/split nodes #"-")]
            {:u (js/parseInt u) :v (js/parseInt v) :w (js/parseInt w)}))
        (clojure.string/split edge-str #",")))

(defn parse-terminals [t-str]
  (set (mapv js/parseInt (clojure.string/split t-str #","))))

(defn find-root [parent x]
  (if (= (nth parent x) x)
    x
    (recur parent (nth parent x))))

(defn steiner-frames
  "使用Kruskal的MST近似Steiner树"
  [edge-str terminal-str]
  (let [edges (sort-by :w (parse-weighted-edges edge-str))
        terminals (parse-terminals terminal-str)
        all-nodes (vec (sort (distinct (mapcat (fn [e] [(:u e) (:v e)]) edges))))
        n (inc (apply max all-nodes))
        frames (atom [])
        parent (atom (vec (range n)))
        steiner-edges (atom #{})]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "Steiner Tree - 初始状态 (终端节点为红色)"
             (concat
               (map (fn [nd]
                      (let [color (if (terminals nd) "#d62728" "#1f77b4")]
                        (str "n" nd " [label=\"" nd "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                    all-nodes)
               (map (fn [e]
                      (str "n" (:u e) " -> n" (:v e)
                           " [dir=none label=\"" (:w e) "\" color=gray]"))
                    edges))))
    ;; Kruskal逐步添加边（优先连接终端节点）
    (let [terminal-edges (filter (fn [e] (or (terminals (:u e)) (terminals (:v e)))) edges)
          other-edges (remove (fn [e] (or (terminals (:u e)) (terminals (:v e)))) edges)
          sorted-edges (concat terminal-edges other-edges)]
      (doseq [edge sorted-edges]
        (let [ru (find-root @parent (:u edge))
              rv (find-root @parent (:v edge))
              accepted (not= ru rv)]
          (when accepted
            (swap! parent assoc ru rv)
            (swap! steiner-edges conj [(:u edge) (:v edge)])
            (swap! frames conj
                   (graphviz/dot-template
                     (str "Steiner Tree - 添加边(" (:u edge) "," (:v edge) ") w=" (:w edge))
                     (concat
                       (map (fn [nd]
                              (let [color (cond
                                            (or (= nd (:u edge)) (= nd (:v edge))) "#ff7f0e"
                                            (terminals nd) "#d62728"
                                            :else "#1f77b4")]
                                (str "n" nd " [label=\"" nd "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                            all-nodes)
                       (map (fn [e]
                              (let [in-tree (@steiner-edges [(:u e) (:v e)])
                                    color (if in-tree "green" "gray")
                                    width (if in-tree "3" "1")]
                                (str "n" (:u e) " -> n" (:v e)
                                     " [dir=none label=\"" (:w e) "\" color=" color " penwidth=" width "]")))
                            edges))))))))
    @frames))

(defn run-steiner-tree! [edge-str terminal-str]
  (let [frames (steiner-frames edge-str terminal-str)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [edge-value (reagent/atom "0-1:2,0-2:4,1-2:1,1-3:3,2-3:5,2-4:2,3-4:1,3-5:4,4-5:3")
                     terminal-value (reagent/atom "0,3,5")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "MST近似" :menu-item-name "mst-approx" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Steiner Tree NP-hard，MST近似比2")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "mst-approx"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @edge-value
                      :on-change #(reset! edge-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "带权边"}]]
            [:div.ml1
             [:input {:value @terminal-value
                      :on-change #(reset! terminal-value (.. % -target -value))
                      :style {:width "6em"}
                      :placeholder "终端节点"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-steiner-tree! @edge-value @terminal-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "Steiner Tree"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

(ns functional-programming-visualgo-fp.algo.computational-geometry
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 计算几何 - 线段交叉检测可视化
;; ============================================================

(defn cross-product [o a b]
  (- (* (- (:x a) (:x o)) (- (:y b) (:y o)))
     (* (- (:y a) (:y o)) (- (:x b) (:x o)))))

(defn on-segment? [p q r]
  (and (<= (min (:x p) (:x r)) (:x q) (max (:x p) (:x r)))
       (<= (min (:y p) (:y r)) (:y q) (max (:y p) (:y r)))))

(defn segments-intersect?
  "检测两条线段是否相交"
  [{:keys [p1 p2]} {:keys [p3 p4]}]
  (let [d1 (cross-product p3 p4 p1)
        d2 (cross-product p3 p4 p2)
        d3 (cross-product p1 p2 p3)
        d4 (cross-product p1 p2 p4)]
    (or (and (< (* d1 d2) 0) (< (* d3 d4) 0))
        (and (zero? d1) (on-segment? p3 p1 p4))
        (and (zero? d2) (on-segment? p3 p2 p4))
        (and (zero? d3) (on-segment? p1 p3 p2))
        (and (zero? d4) (on-segment? p1 p4 p2)))))

(defn parse-segments [seg-str]
  (mapv (fn [s]
          (let [[x1 y1 x2 y2] (mapv js/parseInt (clojure.string/split s #";"))]
            {:p1 {:x x1 :y y1} :p2 {:x x2 :y y2}}))
        (clojure.string/split seg-str #",")))

(defn intersection-frames
  "逐对检测线段交叉"
  [segments]
  (let [n (count segments)
        frames (atom [])
        all-results (atom {})]
    ;; 初始帧
    (swap! frames conj
           (graphviz/dot-template "线段交叉检测 - 初始状态"
             (concat
               (mapcat (fn [i]
                         (let [seg (nth segments i)]
                           [(str "s" i "a [label=\"(" (:x (:p1 seg)) "," (:y (:p1 seg))
                                 ")\" shape=\"point\" fillcolor=\"#1f77b4\" width=0.2]")
                            (str "s" i "b [label=\"(" (:x (:p2 seg)) "," (:y (:p2 seg))
                                 ")\" shape=\"point\" fillcolor=\"#1f77b4\" width=0.2]")
                            (str "s" i "a -> s" i "b [dir=none color=blue label=\"S" i "\"]")]))
                       (range n)))))
    ;; 逐对检测
    (doseq [i (range n)
            j (range (inc i) n)]
      (let [intersect? (segments-intersect?
                         {:p1 (:p1 (nth segments i)) :p2 (:p2 (nth segments i))}
                         {:p3 (:p1 (nth segments j)) :p4 (:p2 (nth segments j))})
            _ (swap! all-results assoc [i j] intersect?)]
        (swap! frames conj
               (graphviz/dot-template
                 (str "检测 S" i " vs S" j (if intersect? " [相交!]" " [不相交]"))
                 (concat
                   (mapcat (fn [k]
                             (let [seg (nth segments k)
                                   is-current (or (= k i) (= k j))
                                   color (cond
                                           is-current "#d62728"
                                           :else "#1f77b4")]
                               [(str "s" k "a [label=\"(" (:x (:p1 seg)) "," (:y (:p1 seg))
                                     ")\" shape=\"point\" fillcolor=\"" color "\" width=0.2]")
                                (str "s" k "b [label=\"(" (:x (:p2 seg)) "," (:y (:p2 seg))
                                     ")\" shape=\"point\" fillcolor=\"" color "\" width=0.2]")
                                (let [edge-color (cond
                                                   (and is-current intersect?) "red"
                                                   is-current "orange"
                                                   :else "blue")]
                                  (str "s" k "a -> s" k "b [dir=none color=" edge-color
                                       " label=\"S" k "\" penwidth=" (if is-current "3" "1") "]"))]))
                           (range n)))))))
    @frames))

(defn run-geometry! [seg-str]
  (let [segments (parse-segments seg-str)
        frames (intersection-frames segments)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [seg-value (reagent/atom "0;0;4;4,0;4;4;0,1;0;1;4")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "交叉检测" :menu-item-name "intersect" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "暴力交叉检测 O(n^2)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "intersect"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @seg-value
                      :on-change #(reset! seg-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "线段(x1;y1;x2;y2,...)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-geometry! @seg-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "计算几何 - 线段交叉检测"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

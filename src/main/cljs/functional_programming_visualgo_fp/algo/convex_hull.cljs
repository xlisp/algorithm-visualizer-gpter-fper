(ns functional-programming-visualgo-fp.algo.convex-hull
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 凸包 - Graham Scan 可视化
;; ============================================================

(defn cross [o a b]
  (- (* (- (:x a) (:x o)) (- (:y b) (:y o)))
     (* (- (:y a) (:y o)) (- (:x b) (:x o)))))

(defn graham-scan-steps
  "Graham扫描法求凸包，记录每一步"
  [points]
  (let [sorted-pts (sort-by (juxt :x :y) points)
        steps (atom [])
        ;; 下凸包
        lower (atom [])]
    (doseq [p sorted-pts]
      (while (and (>= (count @lower) 2)
                  (<= (cross (nth @lower (- (count @lower) 2))
                             (peek @lower) p) 0))
        (swap! lower pop))
      (swap! lower conj p)
      (swap! steps conj {:hull (vec @lower) :current p :phase "下凸包"}))
    ;; 上凸包
    (let [upper (atom [])]
      (doseq [p (reverse sorted-pts)]
        (while (and (>= (count @upper) 2)
                    (<= (cross (nth @upper (- (count @upper) 2))
                               (peek @upper) p) 0))
          (swap! upper pop))
        (swap! upper conj p)
        (swap! steps conj {:hull (vec (concat @lower (rest @upper)))
                           :current p :phase "上凸包"})))
    @steps))

(defn hull-step->dot [points step idx total]
  (let [{:keys [hull current phase]} step
        hull-set (set hull)]
    (graphviz/dot-template
      (str "凸包 " phase " - 步骤 " idx "/" total)
      (concat
        (map-indexed
          (fn [i p]
            (let [color (cond
                          (= p current) "#d62728"
                          (hull-set p) "#2ca02c"
                          :else "#1f77b4")]
              (str "p" i " [label=\"(" (:x p) "," (:y p)
                   ")\" shape=\"point\" fillcolor=\"" color
                   "\" width=0.3 height=0.3]")))
          points)
        (when (> (count hull) 1)
          (map (fn [k]
                 (let [from (.indexOf points (nth hull k))
                       to (.indexOf points (nth hull (inc k)))]
                   (str "p" from " -> p" to " [color=green penwidth=2]")))
               (range (dec (count hull)))))))))

(defn run-convex-hull! [points-str]
  (let [points (mapv (fn [p]
                       (let [[x y] (clojure.string/split p #";")]
                         {:x (js/parseInt x) :y (js/parseInt y)}))
                     (clojure.string/split points-str #","))
        steps (graham-scan-steps points)
        frames (vec (map-indexed
                      (fn [i s] (hull-step->dot points s (inc i) (count steps)))
                      steps))]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [points-value (reagent/atom "0;0,1;3,2;1,3;4,4;2,5;0,3;1,2;3")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "Graham Scan" :menu-item-name "graham" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Graham Scan O(n log n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "graham"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @points-value
                      :on-change #(reset! points-value (.. % -target -value))
                      :style {:width "16em"}
                      :placeholder "点(x;y,x;y)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-convex-hull! @points-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "凸包 - Graham Scan"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

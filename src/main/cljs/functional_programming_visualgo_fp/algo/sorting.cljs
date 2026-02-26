(ns functional-programming-visualgo-fp.algo.sorting
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 冒泡排序可视化
;; ============================================================

(defn bubble-sort-steps
  "执行冒泡排序，记录每一步的状态"
  [arr]
  (let [a (atom (vec arr))
        steps (atom [{:arr (vec arr) :i -1 :j -1 :swapped false}])]
    (doseq [i (range (dec (count arr)))]
      (doseq [j (range (- (count arr) 1 i))]
        (if (> (nth @a j) (nth @a (inc j)))
          (do
            (swap! a (fn [v] (assoc v j (nth v (inc j)) (inc j) (nth v j))))
            (swap! steps conj {:arr (vec @a) :i i :j j :swapped true}))
          (swap! steps conj {:arr (vec @a) :i i :j j :swapped false}))))
    @steps))

(defn sorting-step->dot
  "将排序步骤转换为DOT帧"
  [step idx total]
  (let [{:keys [arr j]} step
        n (count arr)]
    (graphviz/dot-template
      (str "冒泡排序 - 步骤 " idx "/" total)
      (concat
        ["rankdir=LR"]
        (map-indexed
          (fn [k v]
            (let [color (cond
                          (= k j) "#d62728"
                          (= k (inc j)) "#ff7f0e"
                          :else "#1f77b4")]
              (str "n" k " [label=\"" v "\" shape=\"box\" fillcolor=\"" color "\"]")))
          arr)
        (map (fn [k] (str "n" k " -> n" (inc k) " [arrowhead=none]"))
             (range (dec n)))))))

(defn run-bubble-sort! [arr-str]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        steps (bubble-sort-steps arr)
        frames (vec (map-indexed
                      (fn [i s] (sorting-step->dot s (inc i) (count steps)))
                      steps))]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [arr-value (reagent/atom "5,3,8,1,9,2,7,4,6")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "冒泡排序" :menu-item-name "bubble" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "冒泡排序 O(n^2)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "bubble"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @arr-value
                      :on-change #(reset! arr-value (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组(逗号分隔)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-bubble-sort! @arr-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "排序算法 - 冒泡排序"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

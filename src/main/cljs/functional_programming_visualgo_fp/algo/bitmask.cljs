(ns functional-programming-visualgo-fp.algo.bitmask
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 位掩码：子集枚举可视化
;; ============================================================

(defn int->binary-str [n bits]
  (let [s (.toString (js/Math.abs n) 2)]
    (str (apply str (repeat (- bits (count s)) "0")) s)))

(defn subset-enumeration-frames
  "枚举 n 个元素的所有子集，用位掩码表示"
  [n]
  (let [total (js/Math.pow 2 n)]
    (vec
      (for [mask (range total)]
        (let [bits (int->binary-str mask n)
              elements (keep-indexed
                         (fn [i b] (when (= b \1) (str "e" i)))
                         bits)]
          (graphviz/dot-template
            (str "子集枚举 - mask=" mask " (" bits ")")
            (concat
              ["rankdir=LR"]
              (map-indexed
                (fn [i b]
                  (let [color (if (= b \1) "#2ca02c" "#cccccc")]
                    (str "bit" i " [label=\"" b "\" shape=\"box\" fillcolor=\"" color "\"]")))
                bits)
              (map (fn [k] (str "bit" k " -> bit" (inc k) " [arrowhead=none]"))
                   (range (dec n)))
              [(str "subset [label=\"{" (clojure.string/join "," elements) "}\" shape=\"ellipse\" fillcolor=\"#1f77b4\"]")]
              (keep-indexed
                (fn [i b]
                  (when (= b \1) (str "bit" i " -> subset [style=dashed]")))
                bits))))))))

(defn run-bitmask! [n]
  (let [frames (subset-enumeration-frames (min n 4))]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 3)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "子集枚举" :menu-item-name "subset" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "子集枚举 O(2^n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "subset"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "n值"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-bitmask! (js/parseInt @n-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "位掩码 - 子集枚举"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

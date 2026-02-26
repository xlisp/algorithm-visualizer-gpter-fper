(ns functional-programming-visualgo-fp.algo.suffix-array
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 后缀数组可视化
;; ============================================================

(defn build-suffix-array
  "构建后缀数组，返回排序的后缀索引和排序过程"
  [s]
  (let [suffixes (map-indexed (fn [i _] {:idx i :suffix (subs s i)}) s)
        sorted-suffixes (sort-by :suffix suffixes)]
    sorted-suffixes))

(defn suffix-array->dot-frames
  "逐步排序后缀，生成帧"
  [s]
  (let [suffixes (map-indexed (fn [i _] {:idx i :suffix (subs s i)}) s)
        sorted (sort-by :suffix suffixes)
        total (count sorted)]
    (vec
      (concat
        ;; 初始帧：显示所有后缀
        [(graphviz/dot-template
           (str "后缀数组 - 原始后缀 \"" s "\"")
           (concat
             ["rankdir=LR"]
             (map-indexed
               (fn [i {:keys [idx suffix]}]
                 (str "s" i " [label=\"SA[" i "] = '" suffix "' (pos=" idx
                      ")\" shape=\"box\" fillcolor=\"#1f77b4\"]"))
               suffixes)
             (map (fn [k] (str "s" k " -> s" (inc k) " [arrowhead=none]"))
                  (range (dec total)))))]
        ;; 排序后逐步显示
        (for [step (range 1 (inc total))]
          (let [visible (take step sorted)]
            (graphviz/dot-template
              (str "后缀数组 - 排序步骤 " step "/" total)
              (concat
                ["rankdir=LR"]
                (map-indexed
                  (fn [i {:keys [idx suffix]}]
                    (let [color (if (= i (dec step)) "#d62728" "#2ca02c")]
                      (str "s" i " [label=\"SA[" i "]=" idx
                           "\\n'" suffix "'\" shape=\"box\" fillcolor=\"" color "\"]")))
                  visible)
                (when (> (count visible) 1)
                  (map (fn [k] (str "s" k " -> s" (inc k) " [arrowhead=none]"))
                       (range (dec (count visible)))))))))))))

(defn run-suffix-array! [s]
  (let [frames (suffix-array->dot-frames s)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [text-value (reagent/atom "banana")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "构建" :menu-item-name "build" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "后缀数组 构建O(n log n)，查找O(m log n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "build"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @text-value
                      :on-change #(reset! text-value (.. % -target -value))
                      :style {:width "8em"}
                      :placeholder "字符串"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-suffix-array! @text-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "构建"]]}]
      (comps/base-page
        :title "后缀数组"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

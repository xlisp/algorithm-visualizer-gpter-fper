(ns functional-programming-visualgo-fp.algo.fenwick-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 树状数组 (Binary Indexed Tree / Fenwick Tree)
;; ============================================================

(defn lowbit [x]
  (bit-and x (- x)))

(defn build-bit
  "构建树状数组"
  [arr]
  (let [n (count arr)
        bit-arr (atom (vec (repeat (inc n) 0)))]
    (doseq [i (range n)]
      (loop [j (inc i)]
        (when (<= j n)
          (swap! bit-arr update j + (nth arr i))
          (recur (+ j (lowbit j))))))
    @bit-arr))

(defn bit-responsibility
  "计算每个BIT节点负责的区间"
  [n]
  (mapv (fn [i]
          (let [lb (lowbit i)]
            {:idx i :from (- i lb -1) :to i}))
        (range 1 (inc n))))

(defn bit->dot-frames
  "逐步构建树状数组，显示每个节点的管辖范围"
  [arr]
  (let [n (count arr)
        bit-arr (build-bit arr)
        responsibilities (bit-responsibility n)
        total (count responsibilities)]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible (take step responsibilities)
              current (:idx (last visible))]
          (graphviz/dot-template
            (str "树状数组 - 步骤 " step "/" total)
            (concat
              ;; 原数组节点
              (map-indexed
                (fn [i v]
                  (str "a" (inc i) " [label=\"a[" (inc i) "]=" v
                       "\" shape=\"box\" fillcolor=\"#cccccc\"]"))
                arr)
              ;; BIT节点
              (map (fn [{:keys [idx from to]}]
                     (let [color (if (= idx current) "#d62728" "#1f77b4")]
                       (str "b" idx " [label=\"BIT[" idx "]=" (nth bit-arr idx)
                            "\\n[" from "," to "]"
                            "\" shape=\"ellipse\" fillcolor=\"" color "\"]")))
                   visible)
              ;; BIT节点到原数组的管辖边
              (mapcat (fn [{:keys [idx from to]}]
                        (map (fn [j]
                               (str "b" idx " -> a" j " [style=dashed color=gray]"))
                             (range from (inc to))))
                      visible)
              ;; BIT树结构边（子到父）
              (keep (fn [{:keys [idx]}]
                      (let [parent (+ idx (lowbit idx))]
                        (when (<= parent n)
                          (str "b" idx " -> b" parent " [color=blue]"))))
                    visible))))))))

(defn run-fenwick-tree! [arr-str]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (bit->dot-frames arr)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [arr-value (reagent/atom "3,2,5,1,7,4")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "构建" :menu-item-name "build" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "树状数组 更新/查询 O(log n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "build"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @arr-value
                      :on-change #(reset! arr-value (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组(逗号分隔)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-fenwick-tree! @arr-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "构建"]]}]
      (comps/base-page
        :title "树状数组 (Fenwick Tree)"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

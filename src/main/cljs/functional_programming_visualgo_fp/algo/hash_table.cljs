(ns functional-programming-visualgo-fp.algo.hash-table
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 哈希表（链地址法）可视化
;; ============================================================

(defn hash-fn [key size]
  (mod (js/Math.abs key) size))

(defn build-hash-table
  "构建哈希表，返回 buckets"
  [keys size]
  (reduce
    (fn [buckets k]
      (let [idx (hash-fn k size)]
        (update buckets idx (fnil conj []) k)))
    (vec (repeat size []))
    keys))

(defn hash-insert-frames
  "逐步插入元素到哈希表，生成帧"
  [keys size]
  (let [frames (atom [])]
    (loop [remaining keys
           buckets (vec (repeat size []))]
      (if (empty? remaining)
        (do
          (swap! frames conj
                 (graphviz/dot-template
                   "哈希表 - 插入完成"
                   (concat
                     (map (fn [i]
                            (str "b" i " [label=\"桶" i "\" shape=\"box\" fillcolor=\"#1f77b4\"]"))
                          (range size))
                     (mapcat
                       (fn [i]
                         (map-indexed
                           (fn [j v]
                             (str "b" i "v" j " [label=\"" v "\" shape=\"ellipse\" fillcolor=\"#2ca02c\"]"))
                           (nth buckets i)))
                       (range size))
                     (mapcat
                       (fn [i]
                         (let [bucket (nth buckets i)]
                           (if (empty? bucket)
                             []
                             (concat
                               [(str "b" i " -> b" i "v0")]
                               (map (fn [j] (str "b" i "v" j " -> b" i "v" (inc j)))
                                    (range (dec (count bucket))))))))
                       (range size)))))
          @frames)
        (let [k (first remaining)
              idx (hash-fn k size)
              new-buckets (update buckets idx conj k)]
          (swap! frames conj
                 (graphviz/dot-template
                   (str "哈希表 - 插入 " k " -> 桶" idx " (hash=" idx ")")
                   (concat
                     (map (fn [i]
                            (let [color (if (= i idx) "#d62728" "#1f77b4")]
                              (str "b" i " [label=\"桶" i "\" shape=\"box\" fillcolor=\"" color "\"]")))
                          (range size))
                     (mapcat
                       (fn [i]
                         (map-indexed
                           (fn [j v]
                             (let [color (if (and (= i idx) (= j (dec (count (nth new-buckets i)))))
                                           "#ff7f0e" "#2ca02c")]
                               (str "b" i "v" j " [label=\"" v "\" shape=\"ellipse\" fillcolor=\"" color "\"]")))
                           (nth new-buckets i)))
                       (range size))
                     (mapcat
                       (fn [i]
                         (let [bucket (nth new-buckets i)]
                           (if (empty? bucket)
                             []
                             (concat
                               [(str "b" i " -> b" i "v0")]
                               (map (fn [j] (str "b" i "v" j " -> b" i "v" (inc j)))
                                    (range (dec (count bucket))))))))
                       (range size)))))
          (recur (rest remaining) new-buckets))))))

(defn run-hash-table! [arr-str size]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (hash-insert-frames arr size)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [arr-value (reagent/atom "15,7,23,4,11,8,19,3")
                     size-value (reagent/atom 5)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "插入" :menu-item-name "insert" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "哈希表 插入/查找 平均O(1)，最坏O(n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "insert"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @arr-value
                      :on-change #(reset! arr-value (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数据(逗号分隔)"}]]
            [:div.ml1
             [:input {:value @size-value
                      :on-change #(reset! size-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "桶数"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-hash-table! @arr-value (js/parseInt @size-value))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "哈希表 - 链地址法"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

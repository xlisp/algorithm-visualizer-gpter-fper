(ns functional-programming-visualgo-fp.algo.enumeration
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 阿姆斯特朗数 (Armstrong Number)
;; ============================================================

(defn digits [n]
  (map #(js/parseInt %) (seq (str n))))

(defn armstrong? [n]
  (let [ds (digits n)
        k (count ds)]
    (= n (reduce + (map #(Math/pow % k) ds)))))

(defn armstrong-frames
  "检测阿姆斯特朗数的可视化帧"
  [from to]
  (let [candidates (filter #(or (armstrong? %)
                                (zero? (mod % 37))
                                (< % (+ from 5)))
                           (range from (inc to)))
        limited (take 20 candidates)]
    (vec
      (map-indexed
        (fn [idx n]
          (let [ds (digits n)
                k (count ds)
                powers (map #(Math/pow % k) ds)
                sum (reduce + powers)
                is-arm (= n sum)]
            (graphviz/dot-template
              (str "阿姆斯特朗数检测 - " n
                   (if is-arm " - 是阿姆斯特朗数!" " - 不是"))
              (concat
                [(str "num [label=\"" n "\" shape=\"circle\" fillcolor=\""
                      (if is-arm "#2ca02c" "#1f77b4") "\"]")]
                (map-indexed
                  (fn [i d]
                    (str "d" i " [label=\"" d "^" k "=" (int (nth powers i))
                         "\" shape=\"box\" fillcolor=\"#ff7f0e\"]"))
                  ds)
                (map-indexed
                  (fn [i _]
                    (str "num -> d" i))
                  ds)
                [(str "sum [label=\"sum=" (int sum) "\" shape=\"diamond\" fillcolor=\""
                      (if is-arm "#2ca02c" "#d62728") "\"]")]
                (map-indexed
                  (fn [i _] (str "d" i " -> sum"))
                  ds)
                [(str "result [label=\"" (int sum) (if is-arm " = " " != ") n
                      "\" shape=\"box\" fillcolor=\""
                      (if is-arm "#2ca02c" "#d62728") "\"]")
                 "sum -> result"]))))
        limited))))

(defn run-armstrong! []
  (let [frames (armstrong-frames 100 999)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; 百鸡问题 (Hundred Chickens)
;; ============================================================

;; 公鸡5元/只, 母鸡3元/只, 小鸡1元/3只
;; 100元买100只鸡

(defn hundred-chickens-solutions []
  (for [x (range 0 21)       ;; 公鸡最多20只
        y (range 0 34)       ;; 母鸡最多33只
        :let [z (- 100 x y)]
        :when (and (pos? z)
                   (zero? (mod z 3))
                   (= (+ (* 5 x) (* 3 y) (/ z 3)) 100))]
    {:roosters x :hens y :chicks z}))

(defn hundred-chickens-frames []
  (let [solutions (hundred-chickens-solutions)
        ;; 初始帧：问题描述
        init-frame (graphviz/dot-template
                     "百鸡问题: 100元买100只鸡"
                     ["problem [label=\"公鸡5元\\n母鸡3元\\n3只小鸡1元\\n共100元100只\" shape=\"box\" fillcolor=\"#1f77b4\"]"])
        ;; 每个解一帧
        solution-frames
        (map-indexed
          (fn [idx {:keys [roosters hens chicks]}]
            (graphviz/dot-template
              (str "百鸡问题 - 解 " (inc idx) "/" (count solutions))
              [(str "rooster [label=\"公鸡 x " roosters "\\n= " (* 5 roosters) "元"
                    "\" shape=\"box\" fillcolor=\"#d62728\"]")
               (str "hen [label=\"母鸡 x " hens "\\n= " (* 3 hens) "元"
                    "\" shape=\"box\" fillcolor=\"#ff7f0e\"]")
               (str "chick [label=\"小鸡 x " chicks "\\n= " (/ chicks 3) "元"
                    "\" shape=\"box\" fillcolor=\"#2ca02c\"]")
               (str "total_count [label=\"总数: " roosters "+" hens "+" chicks "=100"
                    "\" shape=\"diamond\" fillcolor=\"yellow\"]")
               (str "total_cost [label=\"总价: " (* 5 roosters) "+" (* 3 hens) "+" (/ chicks 3) "=100"
                    "\" shape=\"diamond\" fillcolor=\"yellow\"]")
               "rooster -> total_count"
               "hen -> total_count"
               "chick -> total_count"
               "rooster -> total_cost"
               "hen -> total_cost"
               "chick -> total_cost"]))
          solutions)
        ;; 汇总帧
        summary-frame
        (graphviz/dot-template
          (str "百鸡问题 - 共 " (count solutions) " 种解法")
          (map-indexed
            (fn [idx {:keys [roosters hens chicks]}]
              (str "s" idx " [label=\"公鸡" roosters " 母鸡" hens " 小鸡" chicks
                   "\" shape=\"box\" fillcolor=\"#2ca02c\"]"))
            solutions))]
    (vec (concat [init-frame] solution-frames [summary-frame]))))

(defn run-hundred-chickens! []
  (let [frames (hundred-chickens-frames)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "阿姆斯特朗数" :menu-item-name "armstrong" :click-fn nil}
         {:button-name "百鸡问题" :menu-item-name "chickens" :click-fn nil}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
          :click-fn #(js/alert "阿姆斯特朗数 O(n*d)，百鸡问题 O(n^2)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "armstrong"
         [:div.flex.flex-row {:style {:margin-top "2.5em"}}
          [:div.bg-yellow.ml1.pa1.f6
           {:on-click #(run-armstrong!)
            :class (<class css/hover-menu-style)
            :style {:width "4em"}} "运行"]]
         "chickens"
         [:div.flex.flex-row {:style {:margin-top "2.5em"}}
          [:div.bg-yellow.ml1.pa1.f6
           {:on-click #(run-hundred-chickens!)
            :class (<class css/hover-menu-style)
            :style {:width "4em"}} "运行"]]}]
    (comps/base-page
      :title "枚举算法"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

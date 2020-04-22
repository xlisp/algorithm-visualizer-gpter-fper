(ns functional-programming-visualgo-fp.base.math2
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [functional-programming-visualgo-fp.datas :as datas]
            [functional-programming-visualgo-fp.components :as comps]))


(defn some-coin [kinds-of-coins]
  (cond
    (= kinds-of-coins 1) 1
    (= kinds-of-coins 2) 5
    (= kinds-of-coins 3) 10
    (= kinds-of-coins 4) 25
    (= kinds-of-coins 5) 50))

(comment
  (count-change 5 5) ;; 5 * 1, 1 * 5
  ;; => 2
  (count-change 10 5)                     ;;=> 4种
  ;; 10 * 1, 5 * 2, 1 * 10, 5 * 1 + 1 * 5
  )
(defn count-change
  [amount kinds-of-coins]
  (cond
    (= amount 0) 1
    (or (< amount 0) (= kinds-of-coins 0)) 0
    :else
    (+ (count-change amount (- kinds-of-coins 1))
      (count-change (- amount (some-coin kinds-of-coins)) kinds-of-coins))))

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "可视化计算过程" :menu-item-name "visual-process"
          :click-fn #(js/alert (str "10元,用五种钱来找零,1,5,10,25,50, 共有" (count-change 10 5) "种找零方式: 10 * 1, 5 * 2, 1 * 10, 5 * 1 + 1 * 5" ))}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity" :click-fn  #(js/alert "算法时间复杂度为O(n^m)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "visual-process" [:div]}]
    (comps/base-page
      :title "SICP找零钱问题"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

(ns functional-programming-visualgo-fp.base.math2
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [functional-programming-visualgo-fp.datas :as datas]
            [functional-programming-visualgo-fp.components :as comps]))

(defn some-coin [kinds-of-coins]
  (let [coin
        (cond
          ;; 总共有五种找零的方式: kinds-of-coins是一直递减的
          (= kinds-of-coins 5) 50
          (= kinds-of-coins 4) 25
          (= kinds-of-coins 3) 10
          (= kinds-of-coins 2) 5
          (= kinds-of-coins 1) 1) ]
    coin))

(defonce play-list
  (reagent/atom ["graph {
 splines=line;
    subgraph cluster_0 {
        label=\"零钱的类别\";
        50;
        25;
        10;
        5;
        1;
    }
    subgraph cluster_1 {
        label=\"如何给62元找零钱?\";
        62
    }
    62 -- 50;
    62 -- 25;
    62 -- 10;
    62 -- 5;
    62 -- 1;

}"]))

(comment
  (count-change 5 5)
  ;; 5 * 1, 1 * 5
  ;; => 2 ;; amount为0的次数出现了两次
  (count-change 10 5) ;;=> 4种
  ;; amount为0的次数出现了4次
  ;; 10 * 1, 5 * 2, 1 * 10, 5 * 1 + 1 * 5

  (count-change 62 5)                   ;;=> 77种
  )
(defn count-change
  [amount kinds-of-coins]
  (cond
    (= amount 0) 1
    (or (< amount 0) (= kinds-of-coins 0)) 0
    :else
    (+
      (let [coin-dec (- kinds-of-coins 1)]
        (count-change amount coin-dec))
      (let [amount-dec (- amount (some-coin kinds-of-coins))]
        (count-change amount-dec kinds-of-coins)))))

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "可视化计算过程" :menu-item-name "visual-process"
          :click-fn #(do
                       (graphviz/render-list "#graph" @play-list (atom 0)))}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity" :click-fn  #(js/alert "算法时间复杂度为O(n^m)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "visual-process" [:div]}]
    (comps/base-page
      :title "找零钱问题"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

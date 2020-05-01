(ns functional-programming-visualgo-fp.dot-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

(def target-data
  [[{:id 1 :deep 0} [[{:id 2 :deep 1} []] [{:id 3 :deep 1} []]]]
   [{:id 4 :deep 0} [[{:id 5 :deep 1} [{:id 12 :deep 2} []]]]]
   [{:id 6 :deep 0} []]
   [{:id 7 :deep 0} [[{:id 8 :deep 1} []]
                     [{:id 9 :deep 1} [{:id 11 :deep 2} [{:id 13 :deep 3} []]]]
                     [{:id 10 :deep 1} []]]]])

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "可视化计算过程" :menu-item-name "visual-process" :click-fn nil}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity" :click-fn  #(js/alert "算法时间复杂度为O(n^m)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "visual-process" [:div]}]
    (comps/base-page
      :title "Dot树的操作"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

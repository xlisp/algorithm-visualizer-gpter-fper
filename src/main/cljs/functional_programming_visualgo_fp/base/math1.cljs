(ns functional-programming-visualgo-fp.base.math1
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]))

(defn page []
  [:div
   [panel/header {:title "函数式编程Hello Kid"}]
   [:div.flex.flex-column.justify-center.items-center.mt5
    {:id "graph"}]
   ])

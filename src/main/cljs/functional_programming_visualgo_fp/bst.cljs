(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]))

(defn page []
  [:div.flex.flex-column
   [:div "二叉搜索树"]
   [:div ""]])

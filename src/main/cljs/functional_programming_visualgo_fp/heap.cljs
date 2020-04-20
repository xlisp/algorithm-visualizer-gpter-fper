(ns functional-programming-visualgo-fp.heap
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

(defn page []
  (comps/base-page :title "二叉堆"))

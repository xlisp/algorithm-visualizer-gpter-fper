(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]))

(defn page []
  [:div.flex.flex-column.w-100
   [:div "二叉搜索树"]
   [:div#graphviz]
   [:div.flex.flex-row.pa3
    [:div.flex.flex-auto]
    [:div
     [:button.f5.ba.bg-white
      {:on-click #(let [graph (.querySelector js/document "#graphviz")
                        svg (.querySelector graph "svg")]
                    (do
                      (if svg (.removeChild graph svg) ())
                      (.appendChild graph
                        (graphviz/viz-stri->ele
                          (str "digraph { a -> b; a -> c; c -> " (rand-int 5) ";"
                            (rand-int 5) " -> " (rand-int 5) ";  }" )))))
       :style {:border-radius "1em"
               :height "2em"
               :color "gray"
               :border "2px solid rgba(187, 187, 187, 1)"
               :width "5em"}}
      "生成随机树"]]]])

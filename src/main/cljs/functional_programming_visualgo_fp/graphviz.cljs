(ns functional-programming-visualgo-fp.graphviz
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; (def viz js/Viz)
(def viz identity)

(comment
  (viz-stri->stri "digraph { a -> b; }"))
(defn viz-stri->stri [stri]
  (->
    (viz stri)
    (clojure.string/replace-first #"width=\"\d+pt\"" "width=\"\50%\"")
    (clojure.string/replace-first #"height=\"\d+pt\"" "height=\"\50%\"")))

(comment
  (viz-stri->ele "digraph { a -> b; }")

  #(let [graph (.querySelector js/document "#graphviz")
         svg (.querySelector graph "svg")]
     (do
       (if svg (.removeChild graph svg) ())
       (.appendChild graph
         (graphviz/viz-stri->ele
           (str "digraph { a -> b; a -> c; c -> " (rand-int 5) ";"
             (rand-int 5) " -> " (rand-int 5) ";  }" ))))))
(defn viz-stri->ele
  [st]
  (.-documentElement
    (.parseFromString
      (js/DOMParser.)
      (viz-stri->stri st)
      "image/svg+xml")))

(comment
  (d3-graphviz "#graph" "digraph  {a -> d; a -> c; c -> d}"))
(defn d3-graphviz [id dot]
  (-> js/d3
    (.select id)
    (.graphviz)
    (.renderDot dot)))

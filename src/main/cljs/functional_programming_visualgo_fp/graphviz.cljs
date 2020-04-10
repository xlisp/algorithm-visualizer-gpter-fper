(ns functional-programming-visualgo-fp.graphviz
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(def viz js/Viz)

(comment
  (viz-stri->stri "digraph { a -> b; }"))
(defn viz-stri->stri [stri]
  (->
    (viz stri)
    (clojure.string/replace-first #"width=\"\d+pt\"" "width=\"\50%\"")
    (clojure.string/replace-first #"height=\"\d+pt\"" "height=\"\50%\"")))

(comment
  (viz-stri->ele "digraph { a -> b; }"))
(defn viz-stri->ele
  [st]
  (.-documentElement
    (.parseFromString
      (js/DOMParser.)
      (viz-stri->stri st)
      "image/svg+xml")))

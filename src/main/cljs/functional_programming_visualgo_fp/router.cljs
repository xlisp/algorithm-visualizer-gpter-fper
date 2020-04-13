(ns functional-programming-visualgo-fp.router
  (:require [re-frame.core :as re-frame]
            [reitit.core :as r]
            [reitit.frontend :as rfi]
            [reitit.coercion :as rc]
            [schema.core :as s]
            [reitit.coercion.schema :as rsc]))

(def router
  (r/router
    [["/" :home]
     ["/bst" :bst]
     ["/base-math1" :base-math1]]
    {:compile rc/compile-request-coercers
     :data {:coercion rsc/coercion}}))

(comment
  (switch-router! "/base-math1")
  (switch-router! "/bst"))
(defn switch-router! [loc]
  (set! (.-hash js/window.location) (str "#" loc)))

(ns functional-programming-visualgo-fp.app
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clojure.string :as string]
            [clojure.core.async :as a]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [reitit.core :as reitit]
            [functional-programming-visualgo-fp.events]
            [functional-programming-visualgo-fp.home :as home]
            [functional-programming-visualgo-fp.bst :as bst]
            [functional-programming-visualgo-fp.heap :as heap]
            [functional-programming-visualgo-fp.router :as router]
            [functional-programming-visualgo-fp.base.math1 :as base-math1])
  (:import goog.History
           (goog.date DateTime Interval)))

(def pages
  {:home #'home/page
   :bst #'bst/page
   :heap #'heap/page
   :base-math1 #'base-math1/page})

(defn page []
  [:div
   (let [{:keys [key params]}
         @(re-frame/subscribe [:page])
         page (get pages key)]
     (if page
       [page params]
       [:div "Page Not Found!"]))])

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (let [uri (or (not-empty (string/replace (.-token event) #"^.*#" "")) "/")
              match (reitit/match-by-path router/router uri)
              current-page (:name (:data match))
              route-params (:path-params match)]
          (re-frame/dispatch [:navigate match route-params]))))
    (.setEnabled true)))

(defn mount []
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [:navigate (reitit/match-by-name router/router :home)])
  (hook-browser-navigation!)
  (mount))

(defn after-load []
  (.log js/console "reload")
  (mount))

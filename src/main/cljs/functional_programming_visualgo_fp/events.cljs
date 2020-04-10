(ns functional-programming-visualgo-fp.events
  (:require [re-frame.core :as re-frame]
            [re-frame.db :as re-frame-db]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! go]])
  (:import (goog.date DateTime Interval)))

(re-frame/reg-event-db
  :navigate
  (fn [db [_ route route-params]]
    (assoc db :route {:key route :params route-params})))

(re-frame/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(re-frame/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    {:key (-> route :key :data :name)
     :params (:params route)}))

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


(re-frame/reg-event-db :left-menu
  (fn left-menu
    [db [_ status]]
    (assoc db :left-menu status)))

(re-frame/reg-sub
  :left-menu-status
  (fn [db _]
    (-> db :left-menu)))

(re-frame/reg-event-db :left-menu-item
  (fn left-menu-item
    [db [_ status]]
    (assoc db :left-menu-item status)))

(re-frame/reg-sub
  :left-menu-item-status
  (fn [db _]
    (-> db :left-menu-item)))

(comment
  (do
    (re-frame/dispatch [:left-menu "open"])
    (cljs.pprint/pprint @re-frame.db/app-db)
    (re-frame/subscribe [:left-menu-status]))

  (do
    (re-frame/dispatch [:left-menu-item "create"])
    (cljs.pprint/pprint @re-frame.db/app-db)
    (re-frame/subscribe [:left-menu-item-status]))
  )

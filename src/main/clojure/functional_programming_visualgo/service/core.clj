(ns functional-programming-visualgo.service.core
  (:require [clojure.core.async :as a]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.http-response :as resp]
            [ring.middleware.format :refer [wrap-restful-format]]))

(defn pong [req]
  (prn (:params req))
  (resp/ok {:message "pong!"}))

(def app
  (ring/ring-handler
    (ring/router
      [["/ping" {:middleware [[wrap-restful-format]
                              [wrap-keyword-params]]
                 :post pong
                 :get pong}]
       ["/*" (ring/create-resource-handler)]]
      {:conflicts (constantly nil)})
    (ring/create-default-handler)))

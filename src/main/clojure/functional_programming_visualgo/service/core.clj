(ns functional-programming-visualgo.service.core
  (:require [clojure.core.async :as a]
            [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.core :refer [Expand]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.util.http-response :as resp]
            [ring.middleware.format :refer [wrap-restful-format]]
            [functional-programming-visualgo.service.fp :as fp]
            [functional-programming-visualgo.service.algorithms :as algorithms]))

(if "release"                           ;; TODO
  (extend-protocol Expand
    clojure.lang.Var
    (expand [this opts]
      {:handler (var-get this)}))
  (extend-protocol Expand
    clojure.lang.Var
    (expand [this opts]
      {:handler (fn [& args] (apply (var-get this) args))})))

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
       ["/page/fp/" {:get #'fp/fp-home-page}]
       ["/*" (ring/create-resource-handler)]]
      {:conflicts (constantly nil)})
    (ring/create-default-handler)))

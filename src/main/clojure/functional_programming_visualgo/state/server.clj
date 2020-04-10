(ns functional-programming-visualgo.state.server
  (:require [mount.core :as mount]
            [taoensso.timbre :as timbre]
            [ring.adapter.jetty9 :refer [run-jetty]]
            [functional-programming-visualgo.service.core :refer [app]]))

(mount/defstate server
  :start
  (do
    (prn "Start api server at port: " 3008)
    (run-jetty #'app {:host "0.0.0.0"
                      :port 3008
                      :join? false}))
  :stop
  (.stop server))

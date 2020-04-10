(ns functional-programming-visualgo.core
  (:require [functional-programming-visualgo.state.server :as server]
            [mount.core :as mount]))

(defn -main []
  (prn "Start server...")
  (mount/start #'server/server))

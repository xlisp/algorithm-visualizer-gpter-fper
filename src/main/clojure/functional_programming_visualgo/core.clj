(ns functional-programming-visualgo.core
  (:require [functional-programming-visualgo.state.server :as server]
            [mount.core :as mount]
            [functional-programming-visualgo.state.database :as database]
            [functional-programming-visualgo.state.redis :as redis]))

(defn -main []
  (prn "Start server...")
  (mount/start #'database/*datasource*)
  (mount/start #'server/server))

(comment
  (mount/stop #'server/server)
  (mount/start #'server/server))

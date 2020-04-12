(ns functional-programming-visualgo.state.database
  (:require [functional-programming-visualgo.config :refer [config]]
            [hikari-cp.core :as hikari]
            [next.jdbc :as jdbc]
            [mount.core :as mount]
            [clojure.tools.logging]))

(defn start-datasource []
  (clojure.tools.logging/info "Start datasource...")
  (let [db-config (get-in config [:database])
        ds (jdbc/get-datasource db-config)]
    (jdbc/execute! ds ["select 1"])
    ds))

(mount/defstate ^:dynamic *datasource*
  :start
  (start-datasource)
  :stop
  ;; TODO: close
  (prn *datasource*))

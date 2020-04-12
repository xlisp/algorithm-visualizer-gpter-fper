(ns functional-programming-visualgo.config
  (:require [cprop.core :refer [load-config]]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]))

(def config
  (if (.exists (io/file "config.edn"))
    (load-config :file "config.edn")
    (load-config :file "config.default.edn")))

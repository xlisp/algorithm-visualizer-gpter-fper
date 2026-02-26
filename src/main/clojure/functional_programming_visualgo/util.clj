(ns functional-programming-visualgo.util
  (:require [functional-programming-visualgo.config :refer [config]]
            [functional-programming-visualgo.state.database :refer [*datasource*]]
            [buddy.sign.jwt :as jwt]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [honeysql.helpers :as sql-helpers]
            [honeysql.core :as sql]
            [honeysql.format :as honeysql-fmt]
            [honeysql.types :as sqlt]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as jdbc-sql]
            [next.jdbc.optional :as jdbc-opt]
            [clojure.tools.logging :as log]
            [taoensso.timbre :as timbre]
            [next.jdbc.result-set :refer [ReadableColumn]])
  (:import (java.time LocalDateTime LocalDate LocalTime Instant Duration Period)
           (java.time.format DateTimeFormatter)
           (java.time ZoneId)
           (java.sql Timestamp Time)
           (java.util Date)))

(defmacro log-info [& args]
  `(log/info ~@args))

(defmacro log-debug [& args]
  `(log/debug ~@args))

(defmacro log-error [& args]
  `(log/error ~@args))

(defmacro with-tx [& body]
  `(jdbc/with-transaction [tx# *datasource* {:isolation :serializable}]
     (binding [*datasource* tx#]
       (try
         (log-info "Transaction Begin")
         (let [ret# (do ~@body)]
           (log-info "Transaction Commit")
           ret#)
         (catch RuntimeException ex#
           (log-info "Transaction Rollback!")
           (throw ex#))))))

(defn sql-format
  [sqlmap]
  (sql/format sqlmap
    :namespace-as-table? true
    :quoting :ansi))

(defn sql-execute!
  [sqlmap]
  (let [sqlvec (sql-format sqlmap)]
    (log-debug "SQL: " sqlvec)
    (jdbc/execute! *datasource* sqlvec)))

(defn sql-execute-1!
  [sqlmap]
  (let [sqlvec (sql-format sqlmap)]
    (log-debug "SQL-1: " sqlvec)
    (jdbc/execute-one! *datasource* sqlvec)))

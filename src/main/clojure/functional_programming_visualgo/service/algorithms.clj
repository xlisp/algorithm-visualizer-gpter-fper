(ns functional-programming-visualgo.service.algorithms
  (:require [functional-programming-visualgo.util :as u]
            [honeysql.helpers :as sql]
            [honeysql.types :as sql-type]
            [clojure.string :as str]
            [cheshire.core :as json])
  (:import (java.time LocalDate LocalDateTime)
           (java.time.format DateTimeFormatter)))

(comment
  (get-algorithm-by-id {:id 1})
  ;; => #:algorithms{:id 1,
  ;;                 :name "二叉搜索树",
  ;;                 :desc "二叉树的操作,搜索,删除等等",
  ;;                 :created_at #inst "2020-04-12T17:56:01.000000000-00:00",
  ;;                 :updated_at #inst "2020-04-12T17:56:01.000000000-00:00"}
  )
(defn get-algorithm-by-id
  [{:keys [id]}]
  (-> (sql/select :*)
    (sql/from :algorithms)
    (sql/where [:= :id id])
    (sql/limit 1)
    (u/sql-execute-1!)))

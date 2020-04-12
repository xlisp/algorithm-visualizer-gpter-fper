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

(defn get-algorithms
  []
  (-> (sql/select :*)
    (sql/from :algorithms)
    (u/sql-execute!)))

(comment
  (new-algorithm
    {:name "有向图"
     :desc "有向图的搜索和编辑等操作"}))
(defn new-algorithm
  [{:keys [name desc]}]
  (-> (sql/insert-into :algorithms)
    (sql/values [{:name name
                  :desc desc}])
    (u/sql-execute-1!)))

(comment
  (get-nodes {:algorithm-id 1})
  )
(defn get-nodes
  [{:keys [algorithm-id]}]
  (-> (sql/select :*)
    (sql/from :nodes)
    (sql/where [:= :algorithm algorithm-id])
    (u/sql-execute!)))

(comment
  (get-node-by-id {:id 1}))
(defn get-node-by-id
  [{:keys [id]}]
  (-> (sql/select :*)
    (sql/from :nodes)
    (sql/where [:= :id id])
    (sql/limit 1)
    (u/sql-execute-1!)))

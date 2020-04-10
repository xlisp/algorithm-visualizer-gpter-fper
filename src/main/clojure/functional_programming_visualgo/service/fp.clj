(ns functional-programming-visualgo.service.fp
  (:require [honeysql.helpers :as sql]
            [functional-programming-visualgo.service.layout :as layout]))

(defn fp-home-page [req]
  (:body (layout/render req "public/buffet.html" {})))

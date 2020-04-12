(ns functional-programming-visualgo.state.redis
  (:require [taoensso.carmine :as car :refer (wcar)]
            [functional-programming-visualgo.config :refer [config]]))

(defmacro wcar* [& body]
  `(car/wcar
     (get-in config [:redis])
     ~@body))

(defn del-keys [& args]
  (wcar* (apply car/del args)))

;;; 字符串的存取
(defn get-string [k]
  (wcar* (car/get k)))

(comment
  (set-string "abc" "aaaa")
  (get-string "abc")

  (del-keys "abc")                      ;;=> 1
  )
(defn set-string [k v]
  (wcar* (car/set k v)))

(comment
  (set-expire-stri "aabbcc" "11133431" 20) ;=> ["OK" 1]
  (get-string "aabbcc")                    ;=> 20秒以内是可以获取到值的
  )
(defn set-expire-stri [k v t]
  (wcar*
    (car/set k v)
    (car/expire k t) ))

(ns functional-programming-visualgo-fp.scheme)

;; base ----------
(defn caddr [lis]
  (first (rest (rest lis))))

(defn car [lis]
  (first (take 1 lis)))

(defn cadr [lis]
  (last (take 2 lis)))

;; not base --------

(defn make-tree [left key right]
  (list left key right))

(defn s-key [tree]  (cadr tree))

(defn left [tree]
  (if (empty? tree) '() (car tree)))

(defn right [tree]
  (if (empty? tree) '() (caddr tree)))

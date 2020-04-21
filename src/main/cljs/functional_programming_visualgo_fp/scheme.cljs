(ns functional-programming-visualgo-fp.scheme)

;; base ----------
(defn caddr [lis]
  (first (rest (rest lis))))

(defn car [lis]
  (first (take 1 lis)))

(defn cadr [lis]
  (last (take 2 lis)))

(defn cadddr [lis]
  (first (rest (rest (rest lis)))))

(comment
  (null? 1)
  ;; => false
  (null? [])
  ;; => true
  (null? '(1))
  ;; => false
  (null? nil )
  ;; => true
  )
(defn null? [item]
  (cond (or (seq? item)
          (vector? item)) (empty? item)
        (nil? item) true
        :else false))

(comment
  (fold-left + 0 [1 2 3])      ;;=> 6
  (fold-left conj '() [1 2 3]) ;;=> (3 2 1)
  (fold-right conj [] [1 2 3]) ;;=>[3 2 1]
  )
(defn fold-left [f val coll]
  (if (null? coll) val
      (fold-left f (f val (first coll)) (rest coll))))

(defn fold-right [f val coll]
  (if (null? coll) val
      (f (fold-right f val (rest coll)) (first coll))))

;; not base --------

(defn make-tree [left key right]
  (list left key right))

(defn make-tree-1
  "l: left, s: rank, x: elem, r: right"
  [l s x r]
  (list l s x r))

(defn s-key [tree]  (cadr tree))

(defn left [tree]
  (if (null? tree) '() (car tree)))

(comment
  (right '(() 1 14 ()))
  ;; => 14
  )
(defn right [tree]
  (if (null? tree) '() (caddr tree)))

(comment
  (right-1 '(() 1 14 ())))
(defn right-1 [tree]
  (if (null? tree) '() (cadddr tree)))

(defn rank [t]
  (if (null? t) 0 (cadr t)))

(defn elem [t]
  (if (null? t) '() (caddr t)))

(defn make-node [x a b]
  (if (< (rank a) (rank b))
    (make-tree-1 b (+ (rank a) 1) x a)
    (make-tree-1 a (+ (rank b) 1) x b)))

(defn s-merge [t1 t2]
  (cond (null? t1) t2
        ,
        (null? t2) t1
        ,
        (< (elem t1) (elem t2))
        (make-node (elem t1) (left t1) (s-merge (right-1 t1) t2))
        ,
        :else (make-node (elem t2) (left t2) (s-merge t1 (right-1 t2)))))

(defn insert [t x]
  (s-merge (make-tree-1 '() 1 x '()) t))

(defn find-min [t]
  (elem t))

(defn delete-min [t]
  (s-merge (left t) (right-1 t)))

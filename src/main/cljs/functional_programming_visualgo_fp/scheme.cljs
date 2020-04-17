(ns functional-programming-visualgo-fp.scheme)

(defn caddr [lis]
  (first (rest (rest lis))))

(defn car [lis]
  (first (take 1 lis)))

(defn cadr [lis]
  (last (take 2 lis)))

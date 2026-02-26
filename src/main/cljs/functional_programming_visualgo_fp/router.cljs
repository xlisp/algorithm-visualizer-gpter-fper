(ns functional-programming-visualgo-fp.router
  (:require [re-frame.core :as re-frame]
            [reitit.core :as r]
            [reitit.frontend :as rfi]
            [reitit.coercion :as rc]
            [schema.core :as s]
            [reitit.coercion.schema :as rsc]))

(def router
  (r/router
    [["/" :home]
     ["/bst" :bst]
     ["/heap" :heap]
     ["/dot-tree" :dot-tree]
     ["/base-math1" :base-math1]
     ["/base-math2" :base-math2]
     ["/fibonacci" :fibonacci]
     ["/greedy" :greedy]
     ["/backtracking" :backtracking]
     ["/dp" :dp]
     ["/enumeration" :enumeration]
     ["/divide-conquer" :divide-conquer]
     ;; 基本算法
     ["/sorting" :sorting]
     ["/bitmask" :bitmask]
     ["/linked-list" :linked-list]
     ["/hash-table" :hash-table]
     ["/graph" :graph]
     ["/union-find" :union-find]
     ["/segment-tree" :segment-tree]
     ["/fenwick-tree" :fenwick-tree]
     ["/graph-traversal" :graph-traversal]
     ["/mst" :mst]
     ["/sssp" :sssp]
     ["/network-flow" :network-flow]
     ["/bipartite-matching" :bipartite-matching]
     ["/cycle-finding" :cycle-finding]
     ["/suffix-tree" :suffix-tree]
     ["/suffix-array" :suffix-array]
     ["/convex-hull" :convex-hull]
     ["/min-vertex-cover" :min-vertex-cover]
     ["/tsp" :tsp]
     ["/steiner-tree" :steiner-tree]
     ["/computational-geometry" :computational-geometry]]
    {:compile rc/compile-request-coercers
     :data {:coercion rsc/coercion}}))

(comment
  (switch-router! "/base-math1")
  (switch-router! "/bst"))
(defn switch-router! [loc]
  (set! (.-hash js/window.location) (str "#" loc)))

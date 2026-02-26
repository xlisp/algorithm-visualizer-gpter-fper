(ns functional-programming-visualgo-fp.app
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [clojure.string :as string]
            [clojure.core.async :as a]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [reitit.core :as reitit]
            [functional-programming-visualgo-fp.events]
            [functional-programming-visualgo-fp.home :as home]
            [functional-programming-visualgo-fp.bst :as bst]
            [functional-programming-visualgo-fp.heap :as heap]
            [functional-programming-visualgo-fp.dot-tree :as dot-tree]
            [functional-programming-visualgo-fp.router :as router]
            [functional-programming-visualgo-fp.base.math1 :as base-math1]
            [functional-programming-visualgo-fp.base.math2 :as base-math2]
            [functional-programming-visualgo-fp.algo.fibonacci :as fibonacci]
            [functional-programming-visualgo-fp.algo.greedy :as greedy]
            [functional-programming-visualgo-fp.algo.backtracking :as backtracking]
            [functional-programming-visualgo-fp.algo.dp :as dp]
            [functional-programming-visualgo-fp.algo.enumeration :as enumeration]
            [functional-programming-visualgo-fp.algo.divide-conquer :as divide-conquer]
            ;; 基本算法
            [functional-programming-visualgo-fp.algo.sorting :as sorting]
            [functional-programming-visualgo-fp.algo.bitmask :as bitmask]
            [functional-programming-visualgo-fp.algo.linked-list :as linked-list]
            [functional-programming-visualgo-fp.algo.hash-table :as hash-table]
            [functional-programming-visualgo-fp.algo.graph :as graph]
            [functional-programming-visualgo-fp.algo.union-find :as union-find]
            [functional-programming-visualgo-fp.algo.segment-tree :as segment-tree]
            [functional-programming-visualgo-fp.algo.fenwick-tree :as fenwick-tree]
            [functional-programming-visualgo-fp.algo.graph-traversal :as graph-traversal]
            [functional-programming-visualgo-fp.algo.mst :as mst]
            [functional-programming-visualgo-fp.algo.sssp :as sssp]
            [functional-programming-visualgo-fp.algo.network-flow :as network-flow]
            [functional-programming-visualgo-fp.algo.bipartite-matching :as bipartite-matching]
            [functional-programming-visualgo-fp.algo.cycle-finding :as cycle-finding]
            [functional-programming-visualgo-fp.algo.suffix-tree :as suffix-tree]
            [functional-programming-visualgo-fp.algo.suffix-array :as suffix-array]
            [functional-programming-visualgo-fp.algo.convex-hull :as convex-hull]
            [functional-programming-visualgo-fp.algo.min-vertex-cover :as min-vertex-cover]
            [functional-programming-visualgo-fp.algo.tsp :as tsp]
            [functional-programming-visualgo-fp.algo.steiner-tree :as steiner-tree]
            [functional-programming-visualgo-fp.algo.computational-geometry :as computational-geometry])
  (:import goog.History
           (goog.date DateTime Interval)))

(def pages
  {:home #'home/page
   :bst #'bst/page
   :heap #'heap/page
   :dot-tree #'dot-tree/page
   :base-math1 #'base-math1/page
   :base-math2 #'base-math2/page
   :fibonacci #'fibonacci/page
   :greedy #'greedy/page
   :backtracking #'backtracking/page
   :dp #'dp/page
   :enumeration #'enumeration/page
   :divide-conquer #'divide-conquer/page
   ;; 基本算法
   :sorting #'sorting/page
   :bitmask #'bitmask/page
   :linked-list #'linked-list/page
   :hash-table #'hash-table/page
   :graph #'graph/page
   :union-find #'union-find/page
   :segment-tree #'segment-tree/page
   :fenwick-tree #'fenwick-tree/page
   :graph-traversal #'graph-traversal/page
   :mst #'mst/page
   :sssp #'sssp/page
   :network-flow #'network-flow/page
   :bipartite-matching #'bipartite-matching/page
   :cycle-finding #'cycle-finding/page
   :suffix-tree #'suffix-tree/page
   :suffix-array #'suffix-array/page
   :convex-hull #'convex-hull/page
   :min-vertex-cover #'min-vertex-cover/page
   :tsp #'tsp/page
   :steiner-tree #'steiner-tree/page
   :computational-geometry #'computational-geometry/page})

(defn page []
  [:div
   (let [{:keys [key params]}
         @(re-frame/subscribe [:page])
         page (get pages key)]
     (if page
       [page params]
       [:div "Page Not Found!"]))])

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [^js event]
        (let [uri (or (not-empty (string/replace (.-token ^js event) #"^.*#" "")) "/")
              match (reitit/match-by-path router/router uri)
              current-page (:name (:data match))
              route-params (:path-params match)]
          (re-frame/dispatch [:navigate match route-params]))))
    (.setEnabled true)))

(defn mount []
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init []
  (re-frame/dispatch-sync [:navigate (reitit/match-by-name router/router :home)])
  (hook-browser-navigation!)
  (mount))

(defn after-load []
  (.log js/console "reload")
  (mount))

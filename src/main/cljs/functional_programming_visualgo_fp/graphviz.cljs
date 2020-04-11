(ns functional-programming-visualgo-fp.graphviz
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

;; (def viz js/Viz)
(def viz identity)

(comment
  (viz-stri->stri "digraph { a -> b; }"))
(defn viz-stri->stri [stri]
  (->
    (viz stri)
    (clojure.string/replace-first #"width=\"\d+pt\"" "width=\"\50%\"")
    (clojure.string/replace-first #"height=\"\d+pt\"" "height=\"\50%\"")))

(comment
  (viz-stri->ele "digraph { a -> b; }")

  #(let [graph (.querySelector js/document "#graphviz")
         svg (.querySelector graph "svg")]
     (do
       (if svg (.removeChild graph svg) ())
       (.appendChild graph
         (graphviz/viz-stri->ele
           (str "digraph { a -> b; a -> c; c -> " (rand-int 5) ";"
             (rand-int 5) " -> " (rand-int 5) ";  }" ))))))
(defn viz-stri->ele
  [st]
  (.-documentElement
    (.parseFromString
      (js/DOMParser.)
      (viz-stri->stri st)
      "image/svg+xml")))

(comment
  (d3-graphviz "#graph" "digraph  {a -> d; a -> c; c -> d}"))
(defn d3-graphviz [id dot]
  (-> js/d3
    (.select id)
    (.graphviz)
    (.renderDot dot)))

(comment
  (map
    (fn [item]
      (print (clojure.string/join  "\n" (js->clj item))))
    (array-seq  js/dots))
  )

;; TODO: 需要底层的函数的功能: 能生长 和 删除节点和边的功能
;; 1. 参考https://bl.ocks.org/magjac/4acffdb3afbc4f71b448a210b5060bca来模拟树的生长过程
;; 2. 参考https://bl.ocks.org/magjac/28a70231e2c9dddb84b3b20f450a215f来模拟删除节点和边的过程
;; 3. 参考https://bl.ocks.org/magjac/f485e7b915c9699aa181a11e183f8237来模拟线动态连接和生长过程
(defonce play-list-eg
  (reagent/atom
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\"]
    b [fillcolor=\"#1f77b4\"]
    a -> b
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\"]
    c [fillcolor=\"#2ca02c\"]
    b [fillcolor=\"#1f77b4\"]
    a -> b
    a -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\"]
    b [fillcolor=\"#1f77b4\"]
    c [fillcolor=\"#2ca02c\"]
    a -> b
    a -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\", shape=\"box\"]
    b [fillcolor=\"#1f77b4\", shape=\"parallelogram\"]
    c [fillcolor=\"#2ca02c\", shape=\"pentagon\"]
    a -> b
    a -> c
    b -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"yellow\", shape=\"star\"]
    b [fillcolor=\"yellow\", shape=\"star\"]
    c [fillcolor=\"yellow\", shape=\"star\"]
    a -> b
    a -> c
    b -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\", shape=\"triangle\"]
    b [fillcolor=\"#1f77b4\", shape=\"diamond\"]
    c [fillcolor=\"#2ca02c\", shape=\"trapezium\"]
    a -> b
    a -> c
    b -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\", shape=\"box\"]
    b [fillcolor=\"#1f77b4\", shape=\"parallelogram\"]
    c [fillcolor=\"#2ca02c\", shape=\"pentagon\"]
    a -> b
    a -> c
    b -> c
}"
    "digraph  {
    node [style=\"filled\"]
    a [fillcolor=\"#d62728\"]
    b [fillcolor=\"#1f77b4\"]
    c [fillcolor=\"#2ca02c\"]
    a -> b
    a -> c
    c -> b
}"
    "digraph  {
    node [style=\"filled\"]
    b [fillcolor=\"#1f77b4\"]
    c [fillcolor=\"#2ca02c\"]
    c -> b
}"
    "digraph  {
    node [style=\"filled\"]
    b [fillcolor=\"#1f77b4\"]
}"))

(defn d3-graphviz-render [id])
(defn d3-graphviz-player [id])

(defonce dot-src
  (reagent/atom
    "
digraph {
    graph [label=\"Click on a node or an edge to delete it\" labelloc=\"t\", fontsize=\"20.0\" tooltip=\" \"]
    node [style=\"filled\"]
    Node1 [id=\"NodeId1\" label=\"N1\" fillcolor=\"#d62728\"]
    Node2 [id=\"NodeId2\" label=\"N2\" fillcolor=\"#1f77b4\"]
    Node3 [id=\"NodeId3\" label=\"N3\" fillcolor=\"#2ca02c\"]
    Node4 [id=\"NodeId4\" label=\"N4\" fillcolor=\"#ff7f0e\"]
    Node1 -> Node2 [id=\"EdgeId12\" label=\"E12\"]
    Node1 -> Node3 [id=\"EdgeId131\" label=\"E13\"]
    Node2 -> Node3 [id=\"EdgeId23\" label=\"E23\"]
    Node3 -> Node4 [id=\"EdgeId34\" label=\"E34\"]
}"))


(defn get-node-title [this]
  (->
    js/d3
    (.select this)
    (.selectAll "title")
    (.text)
    (.trim)))

(defn get-node-text [this]
  (->
    js/d3
    (.select this)
    (.selectAll "text")
    (.text)))

(defn get-node-id [this]
  (->
    js/d3
    (.select this)
    (.attr "id")))

(defn get-node-class [this]
  (->
    js/d3
    (.select this)
    (.attr "class")))

(defn interactive []
  (let [nodes (-> js/d3
                ;; 查出来所有的节点和边
                (.selectAll ".node,.edge"))]
    (.on nodes "click"
      (fn []
        (this-as this
          (let [title (get-node-title this)
                text (get-node-text this)
                id (get-node-id this)
                class1 (get-node-class this)]
            ;;
            (js/console.log
              (str "==== title: " title ","
                "text: " text ","
                "id: " id ","
                "class1: " class1 "."))
            ;;
            ))))))

(comment
  (def dot-src-lines (clojure.string/split @dot-src "\n"))

  (def graphviz
    (-> js/d3
      (.select "#graph")
      (.graphviz)))

  (render {:graphviz graphviz :dot-src @dot-src :interactive interactive}))
(defn render
  [{:keys [graphviz dot-src interactive]}]
  (let []
    (-> graphviz
      (.transition
        (fn []
          (-> js/d3
            (.transition)
            (.delay 100)
            (.duration 1000))))
      (.renderDot dot-src)
      (.on "end" interactive))))

(ns functional-programming-visualgo-fp.datas
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(comment
  (map
    (fn [item]
      (print (clojure.string/join  "\n" (js->clj item))))
    (array-seq  js/dots))
  )

(defn graph-tmp [stri]
  "digraph  {
    node [style=\"filled\"]
" stri "
}")

;; TODO: 需要底层的函数的功能: 能生长 和 删除节点和边的功能
;; 1. 参考https://bl.ocks.org/magjac/4acffdb3afbc4f71b448a210b5060bca来模拟树的生长过程
;; 2. 参考https://bl.ocks.org/magjac/28a70231e2c9dddb84b3b20f450a215f来模拟删除节点和边的过程
;; 3. 参考https://bl.ocks.org/magjac/f485e7b915c9699aa181a11e183f8237来模拟线动态连接和生长过程

;; 其他的graphviz的语法:
;; https://graphs.grevian.org/example

(def play-list-data
  [
   "strict graph {
  a -- b
  a -- b
  b -- a [color=blue]
}"
   "graph {
    a -- b;
    b -- c;
    a -- c;
    d -- c;
    e -- c;
    e -- a;
}"
   "graph {
    a -- b;
    b -- c;
    c -- d;
    d -- e;
    e -- f;
    a -- f;
    a -- c;
    a -- d;
    a -- e;
    b -- d;
    b -- e;
    b -- f;
    c -- e;
    c -- f;
    d -- f;
}"
   "digraph {
    a -> b;
    b -> c;
    c -> d;
    d -> a;
}"
   "digraph {
    a -> b[label=\"0.2\",weight=\"0.2\"];
    a -> c[label=\"0.4\",weight=\"0.4\"];
    c -> b[label=\"0.6\",weight=\"0.6\"];
    c -> e[label=\"0.6\",weight=\"0.6\"];
    e -> e[label=\"0.1\",weight=\"0.1\"];
    e -> b[label=\"0.7\",weight=\"0.7\"];
}"
   "graph {
    a -- b[color=red,penwidth=3.0];
    b -- c;
    c -- d[color=red,penwidth=3.0];
    d -- e;
    e -- f;
    a -- d;
    b -- d[color=red,penwidth=3.0];
    c -- f[color=red,penwidth=3.0];
}"
   "graph {
    a -- b -- d -- c -- f[color=red,penwidth=3.0];
    b -- c;
    d -- e;
    e -- f;
    a -- d;
}"
   "digraph {
    subgraph cluster_0 {
        label=\"Subgraph A\";
        a -> b;
        b -> c;
        c -> d;
    }

    subgraph cluster_1 {
        label=\"Subgraph B\";
        a -> f;
        f -> c;
    }
}"
   "
graph {
    splines=line;
    subgraph cluster_0 {
        label=\"Subgraph A\";
        a; b; c
    }

    subgraph cluster_1 {
        label=\"Subgraph B\";
        d; e;
    }

    a -- e;
    a -- d;
    b -- d;
    b -- e;
    c -- d;
    c -- e;
}
"

   "graph {
    rankdir=LR; // Left to Right, instead of Top to Bottom
    a -- { b c d };
    b -- { c e };
    c -- { e f };
    d -- { f g };
    e -- h;
    f -- { h i j g };
    g -- k;
    h -- { o l };
    i -- { l m j };
    j -- { m n k };
    k -- { n r };
    l -- { o m };
    m -- { o p n };
    n -- { q r };
    o -- { s p };
    p -- { s t q };
    q -- { t r };
    r -- t;
    s -- z;
    t -- z;
}"
   "graph {
    rankdir=LR;
    a -- { b c d }; b -- { c e }; c -- { e f }; d -- { f g }; e -- h;
    f -- { h i j g }; g -- k; h -- { o l }; i -- { l m j }; j -- { m n k };
    k -- { n r }; l -- { o m }; m -- { o p n }; n -- { q r };
    o -- { s p }; p -- { s t q }; q -- { t r }; r -- t; s -- z; t -- z;
    { rank=same; b, c, d }
    { rank=same; e, f, g }
    { rank=same; h, i, j, k }
    { rank=same; l, m, n }
    { rank=same; o, p, q, r }
    { rank=same; s, t }
}"
   #_(graph-tmp
       "a -- b
    b -- c
    a -- c
    d -- c
    e -- c
    e -- a ")
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
}"])

(comment
  (reset! play-list-eg play-list-data))
(defonce play-list-eg (reagent/atom play-list-data))

(defn d3-graphviz-render [id])
(defn d3-graphviz-player [id])

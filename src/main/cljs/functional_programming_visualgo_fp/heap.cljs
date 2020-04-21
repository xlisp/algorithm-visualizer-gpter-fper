(ns functional-programming-visualgo-fp.heap
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))


(defn from-list [lst]
  (sch/fold-left sch/insert '() lst))

(defn hsort [t]
  (if (sch/null? t) '() (cons (sch/find-min t) (hsort (sch/delete-min t)))))

(defn heap-sort [lst]
  (hsort (from-list lst)))

(comment
  (from-list '(16 14 10 8 7 9 3 2 4 1))
  ;; => ((((((((() 1 16 ()) 1 14 ()) 1 10 ()) 1 8 ()) 2 7 (() 1 9 ())) 1 3 ()) 2 2 (() 1 4 ())) 1 1 ())
  (heap-sort '(16 14 10 8 7 9 3 2 4 1))
  ;; => (1 2 3 4 7 8 9 10 14 16)
  )

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "创建" :menu-item-name "create" :click-fn nil}
         {:button-name "插入" :menu-item-name "insert" :click-fn nil}
         {:button-name "提取最大值" :menu-item-name "get-max" :click-fn nil}
         {:button-name "堆排序" :menu-item-name "heap-sort" :click-fn nil}
         {:button-name "使用示例" :menu-item-name "usage-example" :click-fn nil}]
        left-menu-item-datas
        {"create" [:div]
         "insert" [:div]}]
    (comps/base-page
      :title "二叉堆"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

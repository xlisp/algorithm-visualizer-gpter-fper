(ns functional-programming-visualgo-fp.heap
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

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

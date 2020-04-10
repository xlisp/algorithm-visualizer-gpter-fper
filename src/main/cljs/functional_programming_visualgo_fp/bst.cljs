(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]))

;; 参考D3.js的领域描述来设计你的公共函数库,你的TODO

(defn select-all-path
  "TODO: 查找特定的一些边,然后改变他们的颜色"
  [])

(defn select-all-circle
  "TODO: 查找特定的一些圆圈,然后改变他们的颜色"
  [])

(defn append-path-circle
  "TODO: 在特定的边后面,加上圆圈"
  [])

(defn append-circle-path
  "TODO: 在特定的圆圈后面,加上边"
  [])

(defn update-circle-text
  "TODO: 修改圆圈内部的text内容"
  [text text2])

(defn page []
  [:div.flex.flex-column.w-100
   [:div "二叉搜索树"]
   [:div#graphviz]
   [:div.flex.flex-row.pa3
    [:div.flex.flex-auto]
    [:div
     [:button.f5.ba.bg-white
      {:on-click #(let [graph (.querySelector js/document "#graphviz")
                        svg (.querySelector graph "svg")]
                    (do
                      (if svg (.removeChild graph svg) ())
                      (.appendChild graph
                        (graphviz/viz-stri->ele
                          (str "digraph { a -> b; a -> c; c -> " (rand-int 5) ";"
                            (rand-int 5) " -> " (rand-int 5) ";  }" )))))
       :style {:border-radius "1em"
               :height "2em"
               :color "gray"
               :border "2px solid rgba(187, 187, 187, 1)"
               :width "5em"}}
      "生成随机树"]]]])

(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]))

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

(defn generate-rand-tree []
  [:div.flex.flex-row.pa3
   [:div.flex.flex-auto]
   [:div
    [:button.f5.ba.bg-white
     {:on-click #(graphviz/d3-graphviz "#graph" "digraph  {4 -> 3; 4 -> 8; 3 -> 1; 8 -> 7; 8 -> 16}")
      :style {:border-radius "1em"
              :height "2em"
              :color "gray"
              :border "2px solid rgba(187, 187, 187, 1)"
              :width "7em"}}
     "生成随机树"]]])

(defn make-tree [left key right]
  (list left key right))

(defn s-key [tree]  (sch/cadr tree))

(defn left [tree]
  (if (empty? tree) '() (sch/car tree)))

(defn right [tree]
  (if (empty? tree) '() (sch/caddr tree)))

(comment
  (s-key (tree-insert (list) 5))        ;=> 5

  (tree-insert (list) 5)
  ;; => (() 5 ())
  (tree-insert (tree-insert (tree-insert (list) 5)  6) 8)
  ;; => (() 5 (() 6 (() 8 ())))

  (def bst-tree (tree-insert (tree-insert (tree-insert (tree-insert (list) 5)  6) 8) 3))
  ;; => ((() 3 ()) 5 (() 6 (() 8 ())))

  (graphviz/d3-graphviz "#graph" "digraph  {5 -> 3; 5 -> 6; 6 -> 8}")

  (graphviz/d3-graphviz "#graph"
    "digraph  {4 -> 3; 4 -> 8; 3 -> 1; 8 -> 7; 8 -> 16; 1 -> 2; 16 -> 10; 10 -> 9; 10 -> 14}")

  (->
    (tree-insert (list) 4)
    (tree-insert 3)
    (tree-insert 8)
    (tree-insert 1)
    (tree-insert 2)
    (tree-insert 7)
    (tree-insert 16)
    (tree-insert 10)
    (tree-insert 9)
    (tree-insert 14))

  ;; =>
  ((((?) 1 ((?) 2 (?))) 3 (?))
   4
   (((?) 7 (?)) 8 ((((?) 9 (?)) 10 ((?) 14 (?))) 16 (?))))


  (right bst-tree)
  ;; => (() 6 (() 8 ()))

  (left bst-tree)
  ;; => (() 3 ())

  ;; 根据中序遍历的结果来生成一颗目标的bst树
  (->
    (tree-insert (list) 4) ;; ROOT节点的值
    ;; 左边的树杈
    (tree-insert 3)
    (tree-insert 1)
    (tree-insert 2)
    ;; 右边的树杈
    (tree-insert 8)
    (tree-insert 7)
    (tree-insert 16)
    (tree-insert 10)
    (tree-insert 9)
    (tree-insert 14))
  ;; => (((() 1 (() 2 ())) 3 ()) 4 ((() 7 ()) 8 (((() 9 ()) 10 (() 14 ())) 16 ())))

  ;; TODO:
  ;; 1. 需要将 树列表 转为dot形式展示出来
  ;; 2. 需要将 dot形式 转为 树列表
  )
(defn tree-insert
  "二叉树的插入"
  [tree x]
  (cond (empty? tree) (list '() x '())
        (< x (s-key tree))
        (doto
	        (make-tree (tree-insert (left tree) x)
		      (s-key tree)
		      (right tree))
          (prn x "左边"))
        (> x (s-key tree))
        (doto
	        (make-tree (left tree)
		      (s-key tree)
		      (tree-insert (right tree) x))
          (prn x "右边"))))

(defn page []
  (reagent/with-let [left-menu (reagent/atom "close")]
    [:div
     [panel/header {:title "二叉搜索树"}]
     (if (= @left-menu "open")
       ;; 用绝对定位来漂浮一个菜单或者弹窗
       [:div.absolute.bottom-0.bg-yellow.mb5
        {:style {:margin-left "2.1em"
                 :height "15em"} }
        [:div.flex.flex-column
         [:div.pa2 {:class (<class css/hover-menu-style)
                    :on-click #(graphviz/d3-graphviz "#graph"
                                 "digraph  {4 -> 3; 4 -> 8; 3 -> 1; 8 -> 7; 8 -> 16; 1 -> 2; 16 -> 10; 10 -> 9; 10 -> 14}")} "创建"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "搜索"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "插入"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "移除"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "中序遍历"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "前序遍历"]
         [:div.pa2 {:class (<class css/hover-menu-style) } "后序遍历"]]]
       [:nobr])
     [:div.flex.flex-row {:style {:height "90vh"}}
      ;; 左边菜单栏
      [:div.flex.flex-column.h-100.bg-black
       {:style {:width "2em"}}
       [:div.flex.flex-auto {:style {:height "60vh"}}]
       [:div.bg-yellow {:style {:height "12.4em"}
                        :on-click #(if (= @left-menu "close")
                                     (reset! left-menu "open")
                                     (reset! left-menu "close"))}
        [:div.pt5 [:img {:src
                         (if (= @left-menu "close")
                           "/img/openRightMini.svg"
                           "/img/openLeftMini.svg")}]]]
       [:div.bg-black {:style {:height "10vh"}}]]
      ;; TODO: svg高度限制不了的问题,外面的盒子高度限制不管用, 但是宽度是能flex的
      [:div.flex.flex-auto.justify-center.items-center.mt3.mb3
       {:style {:height "80vh"}
        :id "graph"}]
      ;; 右边菜单栏
      [:div.bg-black {:style {:width "2em"}}]]
     ;; 底部菜单栏
     [:div.absolute.bottom-0.flex.flex-row.w-100.bg-black
      {:style {:height "2em"}}
      [:div]]]))

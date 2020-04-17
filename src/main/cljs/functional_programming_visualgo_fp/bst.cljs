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
  (def bst-tree
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
      (tree-insert 14)))
  ;; => (((() 1 (() 2 ())) 3 ()) 4 ((() 7 ()) 8 (((() 9 ()) 10 (() 14 ())) 16 ())))

  ;; TODO:
  ;; 1. 需要将 树列表 转为dot形式展示出来
  ;; 2. 需要将 dot形式 转为 树列表

  (take 1 bst-tree)
  (take 2 bst-tree)
  (take 3 bst-tree)

  (first bst-tree)
  (last bst-tree)

  ;; parent = n;
  ;; left = 2n;
  ;; right = 2n + 1;
  (nth bst-tree 0 ) ;;找到左树杈
  (nth bst-tree 1 ) ;; 找到父节点
  (nth bst-tree 2 ) ;;找到右树杈

  (def aaa1  (tree-insert (tree-insert (list) 5)  6))
  (nth aaa1 0 )
  (nth aaa1 1 )
  (nth aaa1 2 )

  (def aaa2  (tree-insert (tree-insert (tree-insert (list) 5) 6) 3))
  (nth aaa2 0 )
  (nth aaa2 1 )
  (nth aaa2 2 )

  (def aaa3   (tree-insert (tree-insert (tree-insert (tree-insert (list) 5) 6) 3) 2))
  (nth aaa3  0)
  (nth aaa3  1)
  (nth aaa3  2)

  (nth (nth aaa3  0) 0)
  (nth (nth aaa3  0) 1)
  (nth (nth aaa3  0) 2)

  (loop [cnt 5
         acc 1
         datas ;; aaa3
         (nth bst-tree 1)
         ]
    (do

      (if (or (empty? datas) (zero? cnt))
        acc
        (recur
          (dec cnt)
          (* acc cnt)
          ;; aaa3
          (do
            (prn "左边" (nth datas 0) "===根" (nth datas 1) "***右边" (nth datas 2))
            (let [left-data (nth datas 0)
                  root (nth datas 1)
                  right-data (nth datas 2)]
              (prn  root " -> " (nth left-data 1) )
              )
            ;; (nth datas 2)
            ;; datas
            (list
              (nth datas 0)
              (nth datas 1)
              (nth datas 2)
              )
            )
          )))
    )

  #_((fn [n]
       (loop [cnt n
              acc 1]
         (if (zero? cnt)
           acc
           (recur (dec cnt)
             (* acc cnt)
             ;; aaa3
             ))))
     5)

  bst-tree

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

(comment
  ;; 树的搜索: 某个节点下面的所有树
  (tree-search bst-tree 9)
  ;; => (() 14 ())
  (tree-search bst-tree 10)
  ;; => ((() 9 ()) 10 (() 14 ()))
  (tree-search bst-tree 8)
  )
(defn tree-search
  "插入的反函数就是搜索,搜索的反函数就是插入: 多分支的递归函数的脚手架"
  [tree x]
  (prn "===" tree "***" (s-key tree))
  (cond (empty? tree) tree
	    (= x (s-key tree)) tree
	    (< x (s-key tree)) (tree-search (left tree) x)
        :else (tree-search (right tree) x)))

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

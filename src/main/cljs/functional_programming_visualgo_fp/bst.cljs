(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

(def dot-stri "digraph  {node [style=\"filled\"];4 -> 3; 4 -> 8; 3 -> 1; 8 -> 7; 8 -> 16; 1 -> 2; 16 -> 10; 10 -> 9; 10 -> 14;")

(comment
  ;; 给二叉树搜索去用的atom list
  (reset! bst-tree-atom []))
(defonce bst-tree-atom (reagent/atom []))

(defonce bst-tree-dots (reagent/atom []))

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
     {:on-click #(graphviz/d3-graphviz "#graph"
                   "digraph  {4 -> 3; 4 -> 8; 3 -> 1; 8 -> 7; 8 -> 16}")
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
  ;; 根据中序遍历的结果来生成一颗目标的bst树
  (def bst-tree
    (->
      (tree-insert (list) 4 tree-insert-cb) ;; ROOT节点的值
      ;; 左边的树杈
      (tree-insert 3 tree-insert-cb)
      (tree-insert 1 tree-insert-cb)
      (tree-insert 2 tree-insert-cb)
      ;; 右边的树杈
      (tree-insert 8 tree-insert-cb)
      (tree-insert 7 tree-insert-cb)
      (tree-insert 16 tree-insert-cb)
      (tree-insert 10 tree-insert-cb)
      (tree-insert 9 tree-insert-cb)
      (tree-insert 14 tree-insert-cb)))
  ;; => (((() 1 (() 2 ())) 3 ()) 4 ((() 7 ()) 8 (((() 9 ()) 10 (() 14 ())) 16 ())))
  )
(defn tree-insert
  "二叉树的插入"
  [tree x op-fn]
  (do
    (op-fn (s-key tree) x)
    (cond (empty? tree) (list '() x '())
          (< x (s-key tree))
          (make-tree (tree-insert (left tree) x op-fn)
		    (s-key tree)
		    (right tree))
          (> x (s-key tree))
          (make-tree (left tree)
		    (s-key tree)
		    (tree-insert (right tree) x op-fn)))))

(comment
  (rand-not-in-bst-tree-val bst-tree))
(defn rand-not-in-bst-tree-val
  "生成一个不在bst-tree上的值"
  [bst-tree]
  (let [ouput-int (atom 1)
        bst-tree-set (set (flatten bst-tree) )]
    (while (bst-tree-set
             (let [rand-num (rand-int 20)]
               (reset! ouput-int rand-num)
               rand-num))
      (prn "find not in bst-tree..."))
    @ouput-int))

(comment
  ;; 树的搜索: 某个节点下面的所有树
  (tree-search bst-tree 9)
  ;; => (() 14 ())
  (tree-search bst-tree 10)
  ;; => ((() 9 ()) 10 (() 14 ()))
  )
(defn tree-search
  "插入的反函数就是搜索,搜索的反函数就是插入: 多分支的递归函数的脚手架"
  [tree x op-fn]
  (do
    (op-fn (s-key tree))
    ;; (prn tree "这里打印出来的是搜索路径高亮:" (s-key tree))
    (cond (empty? tree) tree
	      (= x (s-key tree)) tree
	      (< x (s-key tree)) (tree-search (left tree) x op-fn)
          :else (tree-search (right tree) x op-fn))))

(defn tree-data-init [is-reset-dot op-fn]
  (let [_ (reset! bst-tree-dots [])
        #_(if is-reset-dot
            (reset! bst-tree-dots [])
            nil)
        tree-insert-cb
        (fn [skey x]
          (swap! bst-tree-dots conj [skey x]))
        bst-tree-origin
        (->
          (tree-insert (list) 4 tree-insert-cb) ;; ROOT节点的值
          ;; 左边的树杈
          (tree-insert 3 tree-insert-cb)
          (tree-insert 1 tree-insert-cb)
          (tree-insert 2 tree-insert-cb)
          ;; 右边的树杈
          (tree-insert 8 tree-insert-cb)
          (tree-insert 7 tree-insert-cb)
          (tree-insert 16 tree-insert-cb)
          (tree-insert 10 tree-insert-cb)
          (tree-insert 9 tree-insert-cb)
          (tree-insert 14 tree-insert-cb))
        bst-tree (op-fn bst-tree-origin tree-insert-cb)]
    (do
      (let [datas (rest @bst-tree-dots)
            dot-results (keep-indexed
                          (fn [idx [a b]]
                            (if (nil? a)
                              (nth datas (dec idx))
                              nil))
                          datas)]
        (reset! bst-tree-dots dot-results))
      [bst-tree @bst-tree-dots])))

(defn tree-search-visual [value]
  (let [bst-tree (first (tree-data-init true identity))]
    (do
      (tree-search bst-tree value
        (fn [s-key]
          (swap! bst-tree-atom conj
            (str dot-stri s-key " [fillcolor=\"yellow\"]" "}"))))
      (graphviz/render-list "#graph" @bst-tree-atom (atom 0)))))

(defn show-bst-tree-dots []
  (graphviz/d3-graphviz "#graph"
    (str
      "digraph  {node [style=\"filled\"]; "
      (clojure.string/join
        ";"
        (map (fn [item]
               (str (first item) " -> " (last item))) @bst-tree-dots)) "}")))

(defn create-bst-visual []
  (tree-data-init true identity)
  (show-bst-tree-dots))

(comment
  (insert-bst-visual))
(defn insert-bst-visual []
  (tree-data-init
    false
    (fn [bst-tree-origin tree-insert-cb]
      (let [num (rand-not-in-bst-tree-val bst-tree-origin)]
        (prn "插入值:" num ", s-key: " (s-key bst-tree-origin))
        (tree-insert bst-tree-origin num tree-insert-cb)
        (tree-insert-cb (s-key bst-tree-origin) num))))
  (show-bst-tree-dots))

(defn page []
  (reagent/with-let [search-value (reagent/atom 9)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "创建" :menu-item-name "create" :click-fn #(do (create-bst-visual))}
           {:button-name "插入" :menu-item-name "insert" :click-fn #(do (insert-bst-visual))}
           {:button-name "搜索" :menu-item-name "search" :click-fn nil}
           {:button-name "移除" :menu-item-name "remove" :click-fn nil}
           {:button-name "中序遍历" :menu-item-name "middle-search" :click-fn nil}
           {:button-name "使用示例" :menu-item-name "usage-example" :click-fn nil}]
          left-menu-item-datas
          {"create" [:div]
           "search" [:div.flex.flex-row {:style {:margin-top "4.5em"}}
                     [:div.bg-yellow.pa1.f6
                      {:class (<class css/hover-menu-style)
                       :style {:width "4em"}
                       :on-click
                       (fn []
                         (reset! bst-tree-atom [])
                         (tree-search-visual 16))} "最大值"]
                     [:div.bg-yellow.ml1.pa1.f6
                      {:class (<class css/hover-menu-style)
                       :style {:width "4em"}
                       :on-click (fn []
                                   (reset! bst-tree-atom [])
                                   ;; TODO: 需要flatten然后排序一下
                                   (tree-search-visual 1))} "最小值"]
                     [:div.ml1
                      [:input {:value @search-value
                               :on-change #(reset! search-value (.. % -target -value))
                               :style {:width "5em"}
                               :placeholder "查找值"
                               :type "number"}]]
                     [:div.bg-yellow.ml1.pa1.f6
                      {:on-click (fn []
                                   (reset! bst-tree-atom [])
                                   (tree-search-visual @search-value))
                       :class (<class css/hover-menu-style)
                       :style {:width "4em"}} "查找值"]]}]
      (comps/base-page
        :title "二叉搜索树"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

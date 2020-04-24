(ns functional-programming-visualgo-fp.bst
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

(def dot-head "digraph  {node [style=\"filled\"];")

(comment
  ;; 给二叉树搜索去用的atom list
  (reset! bst-tree-atom []))
(defonce bst-tree-atom (reagent/atom []))

(defonce bst-tree-dots (reagent/atom []))

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

  ;; 就像用reduce一样用fold
  (sch/fold-left tree-insert-1 '() '(4 3 1 2 8 7 16 10 9 14))
  ;; => (((() 1 (() 2 ())) 3 ()) 4 ((() 7 ()) 8 (((() 9 ()) 10 (() 14 ())) 16 ())))
  )
(defn tree-insert
  "二叉树的插入"
  [tree x op-fn]
  (do
    (op-fn (sch/s-key tree) x)
    (cond (empty? tree) (list '() x '())
          (< x (sch/s-key tree))
          (sch/make-tree (tree-insert (sch/left tree) x op-fn)
		    (sch/s-key tree)
		    (sch/right tree))
          (> x (sch/s-key tree))
          (sch/make-tree (sch/left tree)
		    (sch/s-key tree)
		    (tree-insert (sch/right tree) x op-fn)))))

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
    (op-fn (sch/s-key tree))
    (cond (empty? tree) tree
	      (= x (sch/s-key tree)) tree
	      (< x (sch/s-key tree)) (tree-search (sch/left tree) x op-fn)
          :else (tree-search (sch/right tree) x op-fn))))

(defn tree-data-init [is-reset-dot op-fn middle-search-list]
  (let [_ (reset! bst-tree-dots [])
        tree-insert-cb
        (fn [skey x]
          (swap! bst-tree-dots conj [skey x]))
        ;; 复合一个函数tree-insert-cb进去
        tree-insert-fn
        (fn [tree x]
          (tree-insert tree x tree-insert-cb))
        bst-tree-origin
        (sch/fold-left tree-insert-fn '() middle-search-list)
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

(defn tree-search-visual [value middle-search-list]
  (let [bst-tree (first (tree-data-init true identity middle-search-list))
        dot-str (str dot-head
                  (clojure.string/join ";"
                    (map (fn [[a b]] (str a " -> " b)) @bst-tree-dots))
                  ";")]
    (do
      (tree-search bst-tree value
        (fn [s-key]
          (swap! bst-tree-atom conj
            (str dot-str s-key " [fillcolor=\"yellow\"]" "}"))))
      (graphviz/render-list "#graph" @bst-tree-atom (atom 0)))))

(defn show-bst-tree-dots []
  (graphviz/d3-graphviz "#graph"
    (str
      "digraph  {node [style=\"filled\"]; "
      (clojure.string/join
        ";"
        (map (fn [item]
               (str (first item) " -> " (last item))) @bst-tree-dots)) "}")))

(defn create-bst-visual [middle-search-list]
  (tree-data-init true identity middle-search-list)
  (show-bst-tree-dots))

(comment
  (insert-bst-visual))
(defn insert-bst-visual [middle-search-list]
  (tree-data-init
    false
    (fn [bst-tree-origin tree-insert-cb]
      (let [num (rand-not-in-bst-tree-val bst-tree-origin)]
        (prn "插入值:" num ", sch/s-key: " (sch/s-key bst-tree-origin))
        (tree-insert bst-tree-origin num tree-insert-cb)
        (tree-insert-cb (sch/s-key bst-tree-origin) num))) middle-search-list)
  (show-bst-tree-dots))

(defn get-max-val-in-tree [lis]
  (first (sort > lis)))

(defn get-min-val-in-tree [lis]
  (last (sort > lis)))

(defn page []
  (reagent/with-let [search-value (reagent/atom 9)
                     middle-search-list (reagent/atom "4,3,1,2,8,7,16,10,9,14")]
    (let [get-middle-search-list
          (fn []
            (map js/parseInt  (clojure.string/split @middle-search-list #",")))
          left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "创建" :menu-item-name "create" :click-fn nil}
           {:button-name "插入" :menu-item-name "insert" :click-fn #(insert-bst-visual (get-middle-search-list))}
           {:button-name "搜索" :menu-item-name "search" :click-fn nil}
           {:button-name "移除" :menu-item-name "remove" :click-fn nil}
           {:button-name "中序遍历" :menu-item-name "middle-search" :click-fn nil}
           {:button-name "使用示例" :menu-item-name "usage-example" :click-fn nil}]
          left-menu-item-datas
          {"create" [:div.flex.flex-row {:style {:margin-top "2.5em"}}
                     [:div.ml1
                      [:input {:value @middle-search-list
                               :on-change #(reset! middle-search-list (.. % -target -value))
                               :placeholder "中序遍历的顺序列表"}]]
                     [:div.bg-yellow.ml1.pa1.f6
                      {:on-click (fn []
                                   (create-bst-visual (get-middle-search-list)))
                       :class (<class css/hover-menu-style)
                       :style {:width "4em"}} "创建"]]
           "search" [:div.flex.flex-row {:style {:margin-top "6.5em"}}
                     [:div.bg-yellow.pa1.f6
                      {:class (<class css/hover-menu-style)
                       :style {:width "4em"}
                       :on-click
                       (fn []
                         (reset! bst-tree-atom [])
                         (tree-search-visual
                           (get-max-val-in-tree (get-middle-search-list))
                           (get-middle-search-list)))} "最大值"]
                     [:div.bg-yellow.ml1.pa1.f6
                      {:class (<class css/hover-menu-style)
                       :style {:width "4em"}
                       :on-click (fn []
                                   (reset! bst-tree-atom [])
                                   (tree-search-visual
                                     (get-min-val-in-tree (get-middle-search-list))
                                     (get-middle-search-list)))} "最小值"]
                     [:div.ml1
                      [:input {:value @search-value
                               :on-change #(reset! search-value (.. % -target -value))
                               :style {:width "5em"}
                               :placeholder "查找值"
                               :type "number"}]]
                     [:div.bg-yellow.ml1.pa1.f6
                      {:on-click (fn []
                                   (reset! bst-tree-atom [])
                                   (tree-search-visual @search-value (get-middle-search-list)))
                       :class (<class css/hover-menu-style)
                       :style {:width "4em"}} "查找值"]]}]
      (comps/base-page
        :title "二叉搜索树"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

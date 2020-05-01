(ns functional-programming-visualgo-fp.dot-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]
            [functional-programming-visualgo-fp.components :as comps]))

(def target-data
  [[[{:id 1 :deep 0} []] [[{:id 2 :deep 1} []] [{:id 3 :deep 1} []]]]
   [[{:id 4 :deep 0} []] [[{:id 5 :deep 1} [{:id 12 :deep 2} []]]]]
   [[{:id 6 :deep 0} []] []]
   [[{:id 7 :deep 0} []] [[{:id 8 :deep 1} []]
                          [{:id 9 :deep 1} [{:id 11 :deep 2} [{:id 13 :deep 3} []]]]
                          [{:id 10 :deep 1} []]]]])

(comment

  (defn tree-insert-1
    [tree x]
    (cond (empty? tree) (list '() x '())
          (< x (sch/s-key tree))
          (sch/make-tree (tree-insert (sch/left tree) x)
		    (sch/s-key tree)
		    (sch/right tree))
          (> x (sch/s-key tree))
          (sch/make-tree (sch/left tree)
		    (sch/s-key tree)
		    (tree-insert (sch/right tree) x))))

  (->
    (tree-insert (list) 4) ;; ROOT节点的值
    ;; 左边的树杈
    (tree-insert 3)
    (tree-insert 1)
    (tree-insert 2)
    ;; 右边的树杈
    (tree-insert 8)
    ;; (tree-insert 7)
    ;; (tree-insert 16)
    ;; (tree-insert 10)
    ;; (tree-insert 9)
    ;; (tree-insert 14)
    )
  ;; =>
  (((() 1 (() 2 ())) 3 ())
   4
   (() 8 ()))

  (def mydb {
             ;; 深度为1
             1 [[{:id 2 :deep 1} []] [{:id 3 :deep 1} []]] ; 1有一个两个子
             4 [[{:id 5 :deep 1} []]]                      ; 4有一个子
             6 []                                          ; 6无子
             7 [[{:id 8 :deep 1} []]                       ; 7有3个子
                [{:id 9 :deep 1} []]
                [{:id 10 :deep 1} []]]
             ;; 深度为2
             5 [{:id 12 :deep 2} []]
             9 [{:id 11 :deep 2} []]
             ;; 深度为3
             11 [{:id 13 :deep 3} []]})

  (defn make-tree [item]
    [item []])

  (defn make-tree-1 [item]
    [item []])

  ;; x0
  (def init-data
    ;; 深度为0
    [[{:id 1 :deep 0} []]
     [{:id 4 :deep 0} []]
     [{:id 6 :deep 0} []]
     [{:id 7 :deep 0} []]])

  ;;------- 第一阶f1:
  (map
    (fn [[{:keys [id deep] :as item} _]]
      ;; (make-tree (first item))
      [(make-tree item) (mydb id)]
      )
    init-data)

  (defn f1
    "从上面中提炼出来的函数f1"
    [datas]
    (map
      (fn [[{:keys [id deep] :as item} _]]
        [(make-tree item) (mydb id)])
      datas))
  (=  (f1 init-data) data-1)            ;=> true
  ;; => x1
  (def data-1
    [[[{:id 1, :deep 0} []] [[{:id 2, :deep 1} []] [{:id 3, :deep 1} []]]]
     [[{:id 4, :deep 0} []] [[{:id 5, :deep 1} []]]]
     [[{:id 6, :deep 0} []] []]
     [[{:id 7, :deep 0} []] [[{:id 8, :deep 1} []] [{:id 9, :deep 1} []] [{:id 10, :deep 1} []]]]])

  ;; ------ 第二阶f2: 通用化,复用化的极限,

  (map
    (fn [[item datas]]
      ;; 不变: [item datas]
      ;; 变后面的: (f1 datas)
      [item (f1 datas)])
    data-1)

  (defn f2 [datas]
    (map
      (fn [[item datas1]]
        ;; deep为1的datas1
        [item (f1 datas1)])
      datas))

  (=  (f2 data-1) data-2)               ;=> true

  ;; => x2
  (def data-2
    (list [[{:id 1, :deep 0} []] (list [[{:id 2, :deep 1} []] nil] [[{:id 3, :deep 1} []] nil])]
      [[{:id 4, :deep 0} []] (list [[{:id 5, :deep 1} []] [{:id 12, :deep 2} []]])]
      [[{:id 6, :deep 0} []] (list)]
      [[{:id 7, :deep 0} []] (list [[{:id 8, :deep 1} []] nil]
                               [[{:id 9, :deep 1} []] [{:id 11, :deep 2} []]]
                               [[{:id 10, :deep 1} []] nil])]))

  (def data-2-1
    [[[{:id 1, :deep 0} []] [[[{:id 2, :deep 1} []] nil] [[{:id 3, :deep 1} []] nil]]]
     [[{:id 4, :deep 0} []] [[[{:id 5, :deep 1} []] [{:id 12, :deep 2} []]]]]
     [[{:id 6, :deep 0} []] []]
     [[{:id 7, :deep 0} []] [[[{:id 8, :deep 1} []] nil]
                             [[{:id 9, :deep 1} []] [{:id 11, :deep 2} []]]
                             [[{:id 10, :deep 1} []] nil]]]])
  ;; =>    (f1 datas)
  (([[{:id 2, :deep 1} []] nil]
    [[{:id 3, :deep 1} []] nil])
   ([[{:id 5, :deep 1} []] [{:id 12, :deep 2} []]])
   ()
   ([[{:id 8, :deep 1} []] nil]
    [[{:id 9, :deep 1} []] [{:id 11, :deep 2} []]]
    [[{:id 10, :deep 1} []] nil]))

  ;; -------- 第三阶f3: 需要先过滤出来deep=2的值
  (map
    (fn [[item datas]]
      ;; 不变: [item datas]
      ;; 变后面的: (f1 datas)
      ;; [item (f1 datas)]
      ;; (last datas)
      (prn datas "---" (count datas))
      )
    data-2)

  ;; (clojure.walk/postwalk-demo data-2)

  ;; tree map => postwalk : 到2阶以上就很难描述清楚了, 用Postwalk找到递归停止条件: 不同类型的特征工程
  (clojure.walk/postwalk
    (fn [x]
      ;; (prn x)
      (cond
        (and (vector? x) (= (count x) 2))

        (do
          (if (and  (= (last x) [])
                (map?  (first x)))
            (prn "#### " x))

          x)
        ,
        :else x))
    data-2-1)

  ;; "#### " [{:id 1, :deep 0} []]
  ;; "#### " [{:id 2, :deep 1} []]
  ;; "#### " [{:id 3, :deep 1} []]
  ;; "#### " [{:id 4, :deep 0} []]
  ;; "#### " [{:id 5, :deep 1} []]
  ;; "#### " [{:id 12, :deep 2} []]
  ;; "#### " [{:id 6, :deep 0} []]
  ;; "#### " [{:id 7, :deep 0} []]
  ;; "#### " [{:id 8, :deep 1} []]
  ;; "#### " [{:id 9, :deep 1} []]
  ;; "#### " [{:id 11, :deep 2} []]
  ;; "#### " [{:id 10, :deep 1} []]

  ;; 最终f3的样子: 找到一个比较通用的函数
  (clojure.walk/postwalk
    (fn [x]
      ;; (prn x)
      (cond
        (and (vector? x) (= (count x) 2))

        (do
          (if (and  (= (last x) [])
                (map?  (first x))
                ;; 过滤出来deep=2的值
                (= (:deep (first x)) 2))
            (do (prn "#### " x)
                [(first x) (mydb (:id (first x))) ]
                )
            )

          x)
        ,
        :else x))
    data-2-1)
  ;; => x3
  (def data-3
    [[[{:id 1, :deep 0} []] [[[{:id 2, :deep 1} []] nil] [[{:id 3, :deep 1} []] nil]]]
     [[{:id 4, :deep 0} []] [[[{:id 5, :deep 1} []] [{:id 12, :deep 2} []]]]]
     [[{:id 6, :deep 0} []] []]
     [[{:id 7, :deep 0} []] [[[{:id 8, :deep 1} []] nil]
                             [[{:id 9, :deep 1} []] [{:id 11, :deep 2} []]]
                             [[{:id 10, :deep 1} []] nil]]]])

  ;; --------------- 提炼fn : 从f1, f2 ... fn  ----------------
  (clojure.walk/postwalk
    (fn [x]
      ;; (prn x)
      (cond
        (and (vector? x) (= (count x) 2))

        (if (and  (= (last x) [])
              (map?  (first x))
              ;; 过滤出来deep=2的值
              (= (:deep (first x)) 1))
          (do (prn "#### " x "----" (mydb (:id (first x))) "++++" (:id (first x)))
              [(first x) (mydb (:id (first x))) ]
              )
          x)
        ,
        :else x))
    data-1)
  ;; -> 提炼fn : 从f1, f2 ... fn
  (defn f-n [datas deep]
    (clojure.walk/postwalk
      (fn [x]
        ;; (prn x)
        (cond
          (and (vector? x) (= (count x) 2))

          (if (and  (= (last x) [])
                (map?  (first x))
                (= (:deep (first x)) deep))
            (do (prn "#### " x "----" (mydb (:id (first x))) "++++" (:id (first x)))
                [(first x) (mydb (:id (first x))) ]
                )
            x)
          ,
          :else x))
      datas))

  (f-n data-1 1)
  ;; =>
  [[[{:id 1, :deep 0} []] [[{:id 2, :deep 1} nil] [{:id 3, :deep 1} nil]]]
   [[{:id 4, :deep 0} []] [[{:id 5, :deep 1} [{:id 12, :deep 2} []]]]]
   [[{:id 6, :deep 0} []] []]
   [[{:id 7, :deep 0} []] [[{:id 8, :deep 1} nil]
                           [{:id 9, :deep 1} [{:id 11, :deep 2} []]]
                           [{:id 10, :deep 1} nil]]]]

  (f-n data-2 2)
  ;; =>
  ([[{:id 1, :deep 0} []] ([[{:id 2, :deep 1} []] nil] [[{:id 3, :deep 1} []] nil])]
   [[{:id 4, :deep 0} []] ([[{:id 5, :deep 1} []] [{:id 12, :deep 2} nil]])]
   [[{:id 6, :deep 0} []] ()]
   [[{:id 7, :deep 0} []] ([[{:id 8, :deep 1} []] nil]
                           [[{:id 9, :deep 1} []] [{:id 11, :deep 2} [{:id 13, :deep 3} []]]]
                           [[{:id 10, :deep 1} []] nil])])
  ;;

  (f-n [[[{:id 1, :deep 0} []]]
        [[{:id 4, :deep 0} []]]
        [[{:id 6, :deep 0} []]]
        [[{:id 7, :deep 0} []]]] 0)
  ;; =>
  [[[{:id 1, :deep 0} [[{:id 2, :deep 1} []] [{:id 3, :deep 1} []]]]]
   [[{:id 4, :deep 0} [[{:id 5, :deep 1} []]]]]
   [[{:id 6, :deep 0} []]]
   [[{:id 7, :deep 0} [[{:id 8, :deep 1} []]
                       [{:id 9, :deep 1} []]
                       [{:id 10, :deep 1} []]]]]]

  ;; ------- 实现了 (fn .... (f2 (f1 x))) ---------------
  (def x0 [[[{:id 1, :deep 0} []]]
           [[{:id 4, :deep 0} []]]
           [[{:id 6, :deep 0} []]]
           [[{:id 7, :deep 0} []]]])

  (f-n (f-n (f-n x0 0) 1) 2)
  ;; =>
  (def f-n-res
    [[[{:id 1, :deep 0} [[{:id 2, :deep 1} nil] [{:id 3, :deep 1} nil]]]]
     [[{:id 4, :deep 0} [[{:id 5, :deep 1} [{:id 12, :deep 2} nil]]]]]
     [[{:id 6, :deep 0} []]]
     [[{:id 7, :deep 0} [[{:id 8, :deep 1} nil]
                         [{:id 9, :deep 1} [{:id 11, :deep 2} [{:id 13, :deep 3} []]]]
                         [{:id 10, :deep 1} nil]]]]])

  ;; --- 折叠高阶函数起来 ---------
  (= (sch/fold-left f-n x0 '(0 1 2)) f-n-res ) ;=> true


  )
(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "可视化计算过程" :menu-item-name "visual-process" :click-fn nil}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity" :click-fn  #(js/alert "算法时间复杂度为O(n^m)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "visual-process" [:div]}]
    (comps/base-page
      :title "Dot树的操作"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

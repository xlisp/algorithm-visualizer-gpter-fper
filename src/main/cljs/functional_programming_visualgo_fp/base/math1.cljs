(ns functional-programming-visualgo-fp.base.math1
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [functional-programming-visualgo-fp.datas :as datas]))

(defn page []
  [:div
   [panel/header {:title "函数式编程Hello Kid"}]
   [:div.flex.flex-column.justify-center.items-center.mt5
    {:id "graph"}]
   ])

(comment
  (graphviz/render-list

    "#graph"
    [
     ;; 0. 先清空
     (graphviz/dot-template "初始化..."
       (map
         (fn [item]
           (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))
         ["加载中"]))

     ;; 1. 先铺满第一列数据
     (graphviz/dot-template "给一列数字"
       (map
         (fn [item]
           (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))
         [3 4 6 8  13 23 5 12 10 7]))

     ;; 2. 再铺满第二列数据: 没办法控制第二列分开(TODO) ## 可以把箭头的颜色变成透明就行了
     #_(graphviz/dot-template "11111"
         (map
           (fn [item]
             (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))

           (concat  [3 4 6 8  13 23 5 12 10 7]
             (map #(* % %) [3 4 6 8  13 23 5 12 10 7]))
           ))

     ;; 3. 实现第一列和第二列的连接
     (graphviz/dot-template "给这列数字乘以平方"
       (concat
         (map
           (fn [item]
             (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))

           (concat  [3 4 6 8  13 23 5 12 10 7]
             (map #(* % %) [3 4 6 8  13 23 5 12 10 7])))
         (map #(str % " -> " (* % %)
                 " [label=\"平方\"]")
           [3 4 6 8  13 23 5 12 10 7])))

     ;; 4. 将符合`a^2 + b^2 = c^2`的取出来
     (let [selected-stri
           (let  [datas2 (map #(* % %) [3 4 6 8  13 23 5 12 10 7])
                  filter-datas
                  (filter
                    (fn [item]
                      (= (+ (get item 0)
                           (get item 1))
                        (get item 2)))
                    (for [a datas2
                          b  datas2
                          c datas2]
                      [a b c]))
                  uniq-datas (distinct (map sort filter-datas))]
             (map  (fn [[a b c]]
                     (str
                       (str a "" " -> " b "" "\n")
                       (str b "" " -> " c "" "\n")
                       (str c "" " -> " a "" "\n")))
               uniq-datas))]
       (graphviz/dot-template "取出所有符合a^2 + b^2 = c^2的组合出来"
         (concat
           (map
             (fn [item]
               (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))

             (concat  [] #_[3 4 6 8  13 23 5 12 10 7]
               (map #(* % %) [3 4 6 8  13 23 5 12 10 7])))
           #_(map #(str % " -> " (* % %)
                     " [label=\"平方\"]")
               [3 4 6 8  13 23 5 12 10 7])
           selected-stri)))
     ;; 5. 解平方 => label上面可以加上文案
     (let [selected-stri
           (let  [datas2 (map #(* % %) [3 4 6 8  13 23 5 12 10 7])
                  filter-datas
                  (filter
                    (fn [item]
                      (= (+ (get item 0)
                           (get item 1))
                        (get item 2)))
                    (for [a datas2
                          b  datas2
                          c datas2]
                      [a b c]))
                  uniq-datas (distinct (map sort filter-datas))]
             (map  (fn [[a b c]]
                     (let [[a b c] (map js/Math.sqrt [a b c])]
                       (str
                         (str a "" " -> " b "" "\n")
                         (str b "" " -> " c "" "\n")
                         (str c "" " -> " a "" "\n"))))
               uniq-datas))
           ]

       ;; selected-stri
       (graphviz/dot-template "解平方"
         (concat
           (map
             (fn [item]
               (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]"))

             [3 4 6 8  13 23 5 12 10 7]
             #_(concat  [] #_[3 4 6 8  13 23 5 12 10 7]
                 (map #(* % %) [3 4 6 8  13 23 5 12 10 7])))
           #_(map #(str % " -> " (* % %)
                     " [label=\"平方\"]")
               [3 4 6 8  13 23 5 12 10 7])
           selected-stri))
       )
     ;;
     ]

    (atom 0))

  ())

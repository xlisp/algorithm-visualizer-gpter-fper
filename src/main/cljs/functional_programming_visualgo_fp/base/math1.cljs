(ns functional-programming-visualgo-fp.base.math1
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [functional-programming-visualgo-fp.datas :as datas]))

(defn math1 []
  (let [eg-data (map #(+ % (rand-int 10) ) (range 1 40))]
    (graphviz/render-list
      "#graph"
      [(graphviz/dot-circle-tmp :label "初始化..." :datas ["加载中"])
       ;; (graphviz/dot-circle-tmp :label "给一列数字" :datas eg-data)
       (graphviz/dot-template "给一列数字"
         (->>
           eg-data
           (map
             (fn [item]
               (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]")))))

       (graphviz/dot-template "第二列数字"
         (->>
           eg-data
           (concat (map #(* % %) eg-data))
           (map
             (fn [item]
               (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]")))))

       ;; 3. 实现第一列和第二列的连接
       (graphviz/dot-template "给这列数字乘以平方"
         (->>
           eg-data
           (concat (map #(* % %) eg-data))
           (map
             (fn [item]
               (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]")))
           (concat
             (map #(str % " -> " (* % %)
                     " [label=\"平方\"]")
               eg-data))))
       ;; 4. 将符合`a^2 + b^2 = c^2`的取出来
       (let [selected-stri
             (let  [datas2 (map #(* % %) eg-data)
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
           (->>
             eg-data ;; []
             (concat (map #(* % %) eg-data))
             (map
               (fn [item]
                 (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]")))
             (concat selected-stri))))
       ;; 5. 解平方 => label上面可以加上文案
       (let [selected-stri
             (let  [datas2 (map #(* % %) eg-data)
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
                 uniq-datas))]
         (graphviz/dot-template "解平方"
           (->>
             eg-data
             (map
               (fn [item]
                 (str " " item " [shape=\"circle\" label=\"" item " \" fillcolor=\"\"]")))
             (concat selected-stri))))]
      (atom 0))))

(defn page []
  [:div
   [panel/header {:title "函数式编程Hello Kid"}]
   [:div.flex.flex-column.justify-center.items-center.mt5
    {:id "graph"}]
   [:div.flex.flex-row.pa3
    [:div.flex.flex-auto]
    [:div
     [:button.f5.ba.bg-white
      {:on-click math1
       :style {:border-radius "1em"
               :height "2em"
               :color "gray"
               :border "2px solid rgba(187, 187, 187, 1)"
               :width "9em"}}
      "可视化计算过程"]
     [:button.f5.ba.bg-white.ml4
      {:on-click #(js/alert "算法时间复杂度为O(n^3)")
       :style {:border-radius "1em"
               :height "2em"
               :color "gray"
               :border "2px solid rgba(187, 187, 187, 1)"
               :width "9em"}}
      "算法时间复杂度"]]]])

(ns functional-programming-visualgo-fp.home
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.router :as router]))

(defn menu-item
  [& {:keys [title logo]}]
  [:div.flex.justify-center.items-center.pt3.flex-column
   [:div title]
   [:div {:style {:width "8em"}}
    [:img {:src logo}]]])

(defn page []
  [:div
   [:div.bg-black.w-100 {:style {:height "3em"}}
    [:div.flex.flex-row
     [:div.white.pa3.flex.flex-auto "FP Visualgo"]
     [:div.bg-yellow.pa3 {:style {:height "3em"
                                  :width "7em"}} "可视化训练"]
     [:div.pl2
      [:div.bg-yellow.pa3 {:style {:height "3em"
                                   :width "4em"}} "登陆"]]]]
   [:div.flex.flex-column.justify-center.items-center.mt5
    [:div.f2.b.flex.flex-row [:div "FP"] [:div.yellow.pl3 "Visualgo"]]
    [:div.pt2.pb3.f5.gray "函数式编程数据结构和算法动态可视化"]
    [:div.flex.flex-row
     [:input ]
     [:div.pl2
      [:img {:style {:width 25}
             :src "/img/search.svg"}]]]]

   [:div.flex.flex-column.justify-center.items-center.mt5.mb5
    [:div.flex.flex-row
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "排序" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "位掩码" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "链表" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "哈希表" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "二叉堆" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/bst")}
      [menu-item :title "二叉搜索树" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "图结构" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "并查集" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "线段树" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "树状数组" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "递归树/有向无环图" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "图遍历" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "最小生成树" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "单源最短路径" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "网络流" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "二分匹配" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "循环查找" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "后缀树" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "后缀数组" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "计算几何" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "凸体船体" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "最小顶点覆盖" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "Traveling Salesman" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "Steiner Tree" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/base-math1")}
      [menu-item :title "函数式编程Hello Kid" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "其他算法1" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "其他算法2" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "其他算法3" :logo "/img/bst-logo.svg"] ]]]

   [:div.bg-black.w-100 {:style {:height "3em"}}
    [:div.flex.flex-row
     [:div.white.pa3.flex.flex-auto]
     [:div.pa3.white {:style {:height "3em"
                              :width "4em"}} "关于"]
     [:div.pl2
      [:div.pa3.white {:style {:height "3em"
                               :width "4em"}}
       [:a.white {:href "https://github.com/chanshunli/functional-programming-visualgo"} "开源"]]]]]])

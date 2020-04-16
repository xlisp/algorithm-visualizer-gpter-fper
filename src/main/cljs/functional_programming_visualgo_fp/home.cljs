(ns functional-programming-visualgo-fp.home
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.router :as router]))

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
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "排序"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "位掩码"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "链表"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "哈希表"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "二叉堆"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/bst")}
      [:div.flex.justify-center.items-center.pt3.flex-column
       [:div "二叉搜索树"]
       [:div {:style {:width "8em"}}
        [:img {:src "/img/bst-logo.svg"}]]]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "图结构"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "并查集"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "线段树"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "树状数组"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "递归树/有向无环图"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "图遍历"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "最小生成树"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "单源最短路径"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "网络流"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "二分匹配"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "循环查找"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "后缀树"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "后缀数组"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "计算几何"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "凸体船体"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "最小顶点覆盖"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "Traveling Salesman"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "Steiner Tree"]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/base-math1")} "函数式编程Hello Kid"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "其他算法1"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "其他算法2"]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} "其他算法3"]]]

   [:div.bg-black.w-100 {:style {:height "3em"}}
    [:div.flex.flex-row
     [:div.white.pa3.flex.flex-auto]
     [:div.pa3.white {:style {:height "3em"
                              :width "4em"}} "关于"]
     [:div.pl2
      [:div.pa3.white {:style {:height "3em"
                               :width "4em"}}
       [:a.white {:href "https://github.com/chanshunli/functional-programming-visualgo"} "开源"]]]]]])

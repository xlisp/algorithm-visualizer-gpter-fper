(ns functional-programming-visualgo-fp.home
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

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
             :src "/img/search.svg"}]]]]])

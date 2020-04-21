(ns functional-programming-visualgo-fp.panel
  (:require [re-frame.core :as re-frame]))

(defn header [{:keys [title]}]
  (let [left-menu-item (re-frame/subscribe [:left-menu-item-status])]
    [:div.bg-black.w-100 {:style {:height "3em"}}
     [:div.flex.flex-row
      [:div.pa3
       [:img {:style {:width 20}
              :on-click #(.go js/history -1)
              :src "/img/back.svg"}]]
      [:div.white.pa3.flex.flex-auto.flex-row
       [:div title]
       (if (empty? @left-menu-item)
         [:div]
         [:div.flex.flex-row
          [:div.pl2.pr2 "/"]
          [:div @left-menu-item]])]
      [:div.white.pa3 {:style {:height "3em"
                               :width "7em"}} "示例模式"]
      [:div.pl2
       [:div.bg-yellow.pa3 {:style {:height "3em"
                                    :width "4em"}} "登陆"]]]]))

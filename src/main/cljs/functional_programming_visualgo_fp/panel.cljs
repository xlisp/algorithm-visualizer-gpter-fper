(ns functional-programming-visualgo-fp.panel)

(defn header [{:keys [title]}]
  [:div.bg-black.w-100 {:style {:height "3em"}}
   [:div.flex.flex-row
    [:img {:style {:width 20}
           :on-click #(.go js/history -1)
           :src "/img/back.svg"}]
    [:div.white.pa3.flex.flex-auto title]
    [:div.white.pa3 {:style {:height "3em"
                             :width "7em"}} "示例模式"]
    [:div.pl2
     [:div.bg-yellow.pa3 {:style {:height "3em"
                                  :width "4em"}} "登陆"]]]])

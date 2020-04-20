(ns functional-programming-visualgo-fp.components
  (:require [re-frame.core :as re-frame]
            [re-frame.db :as re-frame-db]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

(defn base-page
  [& {:keys [title left-menu-datas left-menu-item-datas]}]
  (let [left-menu (re-frame/subscribe [:left-menu-status])
        left-menu-item (re-frame/subscribe [:left-menu-item-status])]
    [:div
     [panel/header {:title title}]
     [:div.absolute.bottom-0.mb5
      ;; 用绝对定位来漂浮一个菜单或者弹窗: 左边菜单栏
      {:style {:height "15em"} }
      [:div.flex.flex-row
       [:div {:style {:width "2em"}}
        [:div.bg-yellow {:style {:height "15em"}
                         :on-click #(if (= @left-menu "close")
                                      (re-frame/dispatch [:left-menu "open"])
                                      (do (re-frame/dispatch [:left-menu "close"])
                                          (re-frame/dispatch [:left-menu-item "create"])))}
         [:div.h-100.flex
          [:div.flex.justify-center.align-center
           [:img {:style {:height "15em"}
                  :src
                  (if (= @left-menu "close")
                    "/img/openRightMini.svg"
                    "/img/openLeftMini.svg")}]]]]]
       (if (= @left-menu "open")
         [:div.flex.flex-column.bg-yellow.ml1
          (for [{:keys [button-name click-fn menu-item-name]} left-menu-datas]
            [:div.pa2 {:class (<class css/hover-menu-style)
                       :on-click (fn []
                                   (re-frame/dispatch [:left-menu-item menu-item-name])
                                   (if  (fn? click-fn)
                                     (click-fn)))}
             button-name])]
         [:div])
       ;;
       (if (= @left-menu "open")
         [:div.flex.flex-column.ml1
          (if  ((set (keys left-menu-item-datas) ) @left-menu-item)
            (left-menu-item-datas @left-menu-item)
            [:div])]
         [:div])]]
     [:div.flex.flex-row {:style {:height "90vh"}}
      [:div.flex.flex-column.h-100.bg-black
       {:style {:width "2em"}}]
      ;; TODO: svg高度限制不了的问题,外面的盒子高度限制不管用, 但是宽度是能flex的
      [:div.flex.flex-auto.justify-center.items-center.mt3.mb3
       {:style {:height "80vh"}
        :id "graph"}]
      ;; 右边菜单栏
      [:div.bg-black {:style {:width "2em"}}]]
     ;; 底部菜单栏
     [:div.absolute.bottom-0.flex.flex-row.w-100.bg-black
      {:style {:height "2em"}}
      [:div]]]))

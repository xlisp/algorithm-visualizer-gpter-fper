(ns functional-programming-visualgo-fp.heap
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [herb.core :refer [<class join]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]
            [functional-programming-visualgo-fp.scheme :as sch]))


(defn page []
  (reagent/with-let [left-menu (reagent/atom "close")
                     left-menu-item (reagent/atom "create")]
    [:div
     [panel/header {:title "二叉堆"}]
     [:div.absolute.bottom-0.mb5
      ;; 用绝对定位来漂浮一个菜单或者弹窗: 左边菜单栏
      {:style {:height "15em"} }
      [:div.flex.flex-row
       [:div {:style {:width "2em"}}
        [:div.bg-yellow {:style {:height "15em"}
                         :on-click #(if (= @left-menu "close")
                                      (reset! left-menu "open")
                                      (do (reset! left-menu "close")
                                          (reset! left-menu-item "create")))}
         [:div.h-100.flex
          [:div.flex.justify-center.align-center
           [:img {:style {:height "15em"}
                  :src
                  (if (= @left-menu "close")
                    "/img/openRightMini.svg"
                    "/img/openLeftMini.svg")}]]]]]
       (if (= @left-menu "open")
         [:div.flex.flex-column.bg-yellow.ml1
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click #(reset! left-menu-item "graphviz")}
           ;; TODO: 这个后端来保存这个GraphViz图
           "GraphViz图"]
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click
                     #(do
                        (reset! left-menu-item "create")

                        )} "创建"]
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click #(reset! left-menu-item "search")} "搜索"]
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click #(do
                                  (reset! left-menu-item "insert"))} "插入"]
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click #(reset! left-menu-item "remove")} "移除"]
          [:div.pa2 {:class (<class css/hover-menu-style) ;; TODO: 包含前序遍历,还有后序遍历
                     :on-click #(reset! left-menu-item "middle-search")} "中序遍历"]
          [:div.pa2 {:class (<class css/hover-menu-style)
                     :on-click #(reset! left-menu-item "usage-example")} "使用示例"]]
         [:div])
       ;;
       (if (= @left-menu "open")
         [:div.flex.flex-column.ml1
          (case @left-menu-item
            "create" [:div]
            "search" [:div.flex.flex-row {:style {:margin-top "4.5em"}}
                      [:div.bg-yellow.pa1.f6
                       {:class (<class css/hover-menu-style)
                        :style {:width "4em"}
                        :on-click
                        (fn []
                          )} "最大值"]
                      [:div.bg-yellow.ml1.pa1.f6
                       {:class (<class css/hover-menu-style)
                        :style {:width "4em"}
                        :on-click (fn []
                                    )} "最小值"]
                      [:div.ml1
                       [:input {:on-change #(prn (.. % -target -value))
                                :style {:width "5em"}
                                :placeholder "查找值"
                                :type "number"}]]
                      [:div.bg-yellow.ml1.pa1.f6
                       {:on-click (fn []
                                    )
                        :class (<class css/hover-menu-style)
                        :style {:width "4em"}} "查找值"]]
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

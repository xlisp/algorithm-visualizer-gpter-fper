(ns functional-programming-visualgo-fp.home
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.router :as router]))

(defn dividing-line []
  [:div.ml2.mt3 {:style {:width "100vh"}}
   [:hr {:style {:border "thin solid #CCCCCC"}}]])

(defn menu-item
  [& {:keys [title logo]}]
  [:div.flex.justify-center.items-center.pt3.flex-column
   [:div.pb3 title]
   [:div {:style {:width "7em"}}
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
   ;;
   [:div.flex.flex-column.justify-center.items-center.mt5.mb5
    [:div.flex.flex-row {:style {:width "40em"}}
     [:div.mb3.b "基本算法"]
     [:div.flex.flex-auto]]
    [:div.flex.flex-row
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "排序 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "位掩码 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "链表 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "哈希表 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/heap")}
      [menu-item :title "二叉堆" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/bst")}
      [menu-item :title "二叉搜索树" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "图结构 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "并查集 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "线段树 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "树状数组 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "递归树/有向无环图" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "图遍历 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "最小生成树 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "单源最短路径 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "网络流 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "二分匹配 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "循环查找 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "后缀树 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "后缀数组 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "计算几何 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "凸体船体 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "最小顶点覆盖 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "Traveling Salesman" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "Steiner Tree TODO" :logo "/img/bst-logo.svg"] ]]
    [dividing-line]
    [:div.flex.flex-row {:style {:width "40em"}}
     [:div.mb3.b.mt3 "SICP练习题(基本函数式算法)"]
     [:div.flex.flex-auto]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/base-math1")}
      [menu-item :title "一列数找到勾股数组合" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}
                         :on-click #(router/switch-router! "/base-math2")}
      [menu-item :title "找零钱问题" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "斐波那契数列 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "元解释器 TODO" :logo "/img/bst-logo.svg"] ]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "牛顿法求平方根" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "Huffman编码树" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "最大公约数 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "区间算术 TODO" :logo "/img/bst-logo.svg"] ]]
    [dividing-line]
    [:div.flex.flex-row {:style {:width "40em"}}
     [:div.mb3.b.mt3 "深度学习算法"]
     [:div.flex.flex-auto]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "CNN TODO" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "RNN TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "自编码器 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "GNN TODO" :logo "/img/bst-logo.svg"] ]]
    [dividing-line]
    [:div.flex.flex-row {:style {:width "40em"}}
     [:div.mb3.b.mt3 "机器学习算法"]
     [:div.flex.flex-auto]]
    [:div.flex.flex-row.mt3
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "线性回归 TODO" :logo "/img/bst-logo.svg"]]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "逻辑回归 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "多层神经网络 TODO" :logo "/img/bst-logo.svg"] ]
     [:div.shadow-3.ml2 {:style {:width "10em" :height "10em"}} [menu-item :title "SVM TODO" :logo "/img/bst-logo.svg"]]]]

   [:div.bg-black.w-100 {:style {:height "3em"}}
    [:div.flex.flex-row
     [:div.white.pa3.flex.flex-auto]
     [:div.pa3.white {:style {:height "3em"
                              :width "4em"}} "关于"]
     [:div.pl2
      [:div.pa3.white {:style {:height "3em"
                               :width "4em"}}
       [:a.white {:href "https://github.com/chanshunli/functional-programming-visualgo"} "开源"]]]]]])

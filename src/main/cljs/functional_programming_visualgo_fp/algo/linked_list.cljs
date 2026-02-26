(ns functional-programming-visualgo-fp.algo.linked-list
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 链表操作可视化
;; ============================================================

(defn list->dot-frame
  "将链表显示为DOT帧，highlight-idx 高亮某个节点"
  [lst highlight-idx label]
  (graphviz/dot-template
    label
    (concat
      ["rankdir=LR"]
      (map-indexed
        (fn [i v]
          (let [color (cond
                        (= i highlight-idx) "#d62728"
                        :else "#1f77b4")]
            (str "n" i " [label=\"" v "\" shape=\"record\" fillcolor=\"" color "\"]")))
        lst)
      (map (fn [k] (str "n" k " -> n" (inc k)))
           (range (dec (count lst))))
      [(str "null [label=\"NULL\" shape=\"plaintext\" fillcolor=\"white\"]")
       (str "n" (dec (count lst)) " -> null")])))

(defn insert-frames
  "在链表指定位置插入元素，生成逐步帧"
  [lst pos val]
  (let [frames (atom [])
        n (count lst)]
    ;; 搜索阶段
    (doseq [i (range (min pos n))]
      (swap! frames conj
             (list->dot-frame lst i (str "链表插入 - 搜索位置 " i))))
    ;; 插入
    (let [new-lst (vec (concat (take pos lst) [val] (drop pos lst)))]
      (swap! frames conj
             (list->dot-frame new-lst pos (str "链表插入 - 在位置 " pos " 插入 " val))))
    @frames))

(defn search-frames
  "在链表中搜索元素，生成逐步帧"
  [lst target]
  (vec
    (for [i (range (count lst))]
      (let [found (= (nth lst i) target)]
        (list->dot-frame lst i
                         (if found
                           (str "链表搜索 - 找到 " target " 在位置 " i)
                           (str "链表搜索 - 检查位置 " i)))))))

(defn delete-frames
  "在链表中删除元素，生成逐步帧"
  [lst pos]
  (let [frames (atom [])]
    (doseq [i (range (min pos (count lst)))]
      (swap! frames conj
             (list->dot-frame lst i (str "链表删除 - 搜索位置 " i))))
    (let [new-lst (vec (concat (take pos lst) (drop (inc pos) lst)))]
      (swap! frames conj
             (list->dot-frame new-lst -1 (str "链表删除 - 删除位置 " pos " 完成"))))
    @frames))

(defn run-insert! [arr-str pos val]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (insert-frames arr pos val)]
    (graphviz/render-list "#graph" frames (atom 0))))

(defn run-search! [arr-str target]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (search-frames arr target)]
    (graphviz/render-list "#graph" frames (atom 0))))

(defn run-delete! [arr-str pos]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (delete-frames arr pos)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [list-value (reagent/atom "1,3,5,7,9")
                     insert-pos (reagent/atom 2)
                     insert-val (reagent/atom 4)
                     search-val (reagent/atom 5)
                     delete-pos (reagent/atom 2)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "插入" :menu-item-name "insert" :click-fn nil}
           {:button-name "搜索" :menu-item-name "search" :click-fn nil}
           {:button-name "删除" :menu-item-name "delete" :click-fn nil}]
          left-menu-item-datas
          {"graphviz" [:div]
           "insert"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @list-value
                      :on-change #(reset! list-value (.. % -target -value))
                      :style {:width "8em"}
                      :placeholder "链表"}]]
            [:div.ml1
             [:input {:value @insert-pos
                      :on-change #(reset! insert-pos (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "位置"
                      :type "number"}]]
            [:div.ml1
             [:input {:value @insert-val
                      :on-change #(reset! insert-val (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "值"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-insert! @list-value (js/parseInt @insert-pos) (js/parseInt @insert-val))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "插入"]]
           "search"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @search-val
                      :on-change #(reset! search-val (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "查找值"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-search! @list-value (js/parseInt @search-val))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "搜索"]]
           "delete"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @delete-pos
                      :on-change #(reset! delete-pos (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "位置"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-delete! @list-value (js/parseInt @delete-pos))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "删除"]]}]
      (comps/base-page
        :title "链表"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

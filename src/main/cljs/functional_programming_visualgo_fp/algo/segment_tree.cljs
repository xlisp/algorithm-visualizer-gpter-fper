(ns functional-programming-visualgo-fp.algo.segment-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 线段树可视化（区间求和）
;; ============================================================

(defn build-seg-tree
  "构建线段树节点列表 [{:id :l :r :sum}]"
  [arr]
  (let [n (count arr)
        tree (atom {})]
    (letfn [(build [id l r]
              (if (= l r)
                (do (swap! tree assoc id {:id id :l l :r r :sum (nth arr l)})
                    (nth arr l))
                (let [mid (quot (+ l r) 2)
                      left-sum (build (* 2 id) l mid)
                      right-sum (build (inc (* 2 id)) (inc mid) r)
                      s (+ left-sum right-sum)]
                  (swap! tree assoc id {:id id :l l :r r :sum s})
                  s)))]
      (build 1 0 (dec n)))
    @tree))

(defn seg-tree->dot-frames
  "逐步构建线段树，生成帧"
  [arr]
  (let [tree (build-seg-tree arr)
        sorted-ids (sort (keys tree))
        total (count sorted-ids)]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible-ids (set (take step sorted-ids))
              current-id (nth sorted-ids (dec step))]
          (graphviz/dot-template
            (str "线段树(区间和) - 步骤 " step "/" total)
            (concat
              (map (fn [id]
                     (let [node (get tree id)
                           color (cond
                                   (= id current-id) "#d62728"
                                   (= (:l node) (:r node)) "#2ca02c"
                                   :else "#1f77b4")]
                       (str "n" id " [label=\"[" (:l node) "," (:r node) "]\\nsum=" (:sum node)
                            "\" shape=\"box\" fillcolor=\"" color "\"]")))
                   (filter visible-ids sorted-ids))
              (keep (fn [id]
                      (let [left (* 2 id)
                            right (inc (* 2 id))]
                        (when (and (visible-ids left) (visible-ids id))
                          (str "n" id " -> n" left))))
                    (filter visible-ids sorted-ids))
              (keep (fn [id]
                      (let [right (inc (* 2 id))]
                        (when (and (visible-ids right) (visible-ids id))
                          (str "n" id " -> n" right))))
                    (filter visible-ids sorted-ids)))))))))

(defn run-segment-tree! [arr-str]
  (let [arr (mapv js/parseInt (clojure.string/split arr-str #","))
        frames (seg-tree->dot-frames arr)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [arr-value (reagent/atom "1,3,5,7,9,11")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "构建" :menu-item-name "build" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "线段树 构建O(n)，查询/更新O(log n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "build"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @arr-value
                      :on-change #(reset! arr-value (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组(逗号分隔)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-segment-tree! @arr-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "构建"]]}]
      (comps/base-page
        :title "线段树"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

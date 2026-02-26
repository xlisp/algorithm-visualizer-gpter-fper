(ns functional-programming-visualgo-fp.algo.cycle-finding
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; Floyd 循环查找算法 (龟兔赛跑) 可视化
;; ============================================================

(defn make-linked-list-with-cycle
  "创建带环的链表 next数组，cycle-start 是环的起点索引"
  [n cycle-start]
  (let [next-arr (vec (concat (map inc (range (dec n))) [cycle-start]))]
    next-arr))

(defn floyd-frames
  "Floyd算法逐步执行，生成帧"
  [n cycle-start]
  (let [next-arr (make-linked-list-with-cycle n cycle-start)
        frames (atom [])
        make-dot (fn [slow fast label phase]
                   (graphviz/dot-template
                     label
                     (concat
                       ["rankdir=LR"]
                       (map (fn [i]
                              (let [color (cond
                                            (and (= i slow) (= i fast)) "#ff7f0e"
                                            (= i slow) "#2ca02c"
                                            (= i fast) "#d62728"
                                            (= i cycle-start) "#ffff00"
                                            :else "#1f77b4")]
                                (str "n" i " [label=\"" i
                                     (when (= i slow) " 🐢")
                                     (when (= i fast) " 🐇")
                                     "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                            (range n))
                       (map (fn [i]
                              (str "n" i " -> n" (nth next-arr i)))
                            (range n)))))]
    ;; 初始帧
    (swap! frames conj (make-dot 0 0 "Floyd循环检测 - 初始状态" :init))
    ;; Phase 1: 快慢指针相遇
    (loop [slow 0 fast 0 step 0]
      (let [new-slow (nth next-arr slow)
            new-fast (nth next-arr (nth next-arr fast))]
        (swap! frames conj
               (make-dot new-slow new-fast
                         (str "Phase1 - 步骤" (inc step) " 慢=" new-slow " 快=" new-fast) :phase1))
        (when (and (not= new-slow new-fast) (< step 30))
          (recur new-slow new-fast (inc step)))))
    ;; Phase 2: 找环入口
    (let [;; 找到相遇点
          meet-point (loop [slow 0 fast 0 first? true]
                       (let [s (if first? (nth next-arr slow) (nth next-arr slow))
                             f (if first? (nth next-arr (nth next-arr fast)) (nth next-arr (nth next-arr fast)))]
                         (if (and (not first?) (= slow fast))
                           slow
                           (recur s f false))))]
      (loop [p1 0 p2 meet-point step 0]
        (when (and (not= p1 p2) (< step 30))
          (let [np1 (nth next-arr p1)
                np2 (nth next-arr p2)]
            (swap! frames conj
                   (make-dot np1 np2
                             (str "Phase2 - 找环入口 p1=" np1 " p2=" np2) :phase2))
            (recur np1 np2 (inc step)))))
      (swap! frames conj
             (make-dot cycle-start cycle-start
                       (str "检测完成 - 环入口在节点 " cycle-start) :done)))
    @frames))

(defn run-cycle-finding! [n cycle-start]
  (let [frames (floyd-frames n cycle-start)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 8)
                     cycle-start (reagent/atom 3)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "Floyd检测" :menu-item-name "floyd" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "Floyd循环检测 O(n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "floyd"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "节点数"
                      :type "number"}]]
            [:div.ml1
             [:input {:value @cycle-start
                      :on-change #(reset! cycle-start (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "环起点"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-cycle-finding! (js/parseInt @n-value) (js/parseInt @cycle-start))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "循环查找 - Floyd算法"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

(ns functional-programming-visualgo-fp.algo.fibonacci
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 纯算法函数：斐波那契递归，带回调捕获调用树
;; ============================================================

(defn fib-tree
  "构建斐波那契递归调用树，返回 {:id :label :value :children []}"
  [n counter]
  (let [id (swap! counter inc)]
    (if (<= n 1)
      {:id id :n n :value n :children []}
      (let [left (fib-tree (- n 1) counter)
            right (fib-tree (- n 2) counter)]
        {:id id :n n :value (+ (:value left) (:value right))
         :children [left right]}))))

;; ============================================================
;; DOT 生成：逐步展开调用树
;; ============================================================

(defn collect-nodes
  "BFS顺序收集树的所有节点"
  [tree]
  (loop [queue [tree] result []]
    (if (empty? queue)
      result
      (let [node (first queue)
            children (:children node)]
        (recur (concat (rest queue) children)
               (conj result node))))))

(defn tree->dot-frames
  "将调用树转换为逐步展开的DOT帧列表"
  [tree]
  (let [all-nodes (collect-nodes tree)
        total (count all-nodes)]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible-nodes (take step all-nodes)
              visible-ids (set (map :id visible-nodes))
              current-id (:id (last visible-nodes))
              resolved-ids (set (map :id (filter #(empty? (:children %)) visible-nodes)))]
          (graphviz/dot-template
            (str "Fibonacci递归调用树 - 步骤 " step "/" total)
            (concat
              ;; 节点定义
              (map (fn [node]
                     (let [color (cond
                                  (= (:id node) current-id) "#d62728"
                                  (resolved-ids (:id node)) "#2ca02c"
                                  :else "#1f77b4")]
                       (str "n" (:id node)
                            " [label=\"fib(" (:n node) ")"
                            (when (resolved-ids (:id node))
                              (str "=" (:value node)))
                            "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                   visible-nodes)
              ;; 边定义
              (mapcat (fn [node]
                        (keep (fn [child]
                                (when (visible-ids (:id child))
                                  (str "n" (:id node) " -> n" (:id child))))
                              (:children node)))
                      visible-nodes))))))))

;; 最终帧：所有节点显示计算结果，全部绿色
(defn tree->final-frame [tree]
  (let [all-nodes (collect-nodes tree)]
    (graphviz/dot-template
      (str "Fibonacci(" (:n tree) ") = " (:value tree) " - 计算完成")
      (concat
        (map (fn [node]
               (str "n" (:id node)
                    " [label=\"fib(" (:n node) ")=" (:value node)
                    "\" shape=\"circle\" fillcolor=\"#2ca02c\"]"))
             all-nodes)
        (mapcat (fn [node]
                  (map (fn [child]
                         (str "n" (:id node) " -> n" (:id child)))
                       (:children node)))
                all-nodes)))))

;; ============================================================
;; 可视化编排
;; ============================================================

(defn run-fibonacci! [n]
  (let [counter (atom 0)
        tree (fib-tree n counter)
        frames (conj (tree->dot-frames tree) (tree->final-frame tree))]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 5)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "斐波那契" :menu-item-name "visual-process" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "时间复杂度 O(2^n)，空间复杂度 O(n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "visual-process"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "n值"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-fibonacci! (min 7 (js/parseInt @n-value)))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "递归算法 - 斐波那契"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

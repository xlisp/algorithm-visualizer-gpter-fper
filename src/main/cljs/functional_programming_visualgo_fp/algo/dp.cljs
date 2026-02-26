(ns functional-programming-visualgo-fp.algo.dp
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 爬楼梯问题 (Climbing Stairs)
;; ============================================================

(defn climb-tree
  "构建爬楼梯的递归调用树"
  [n counter]
  (let [id (swap! counter inc)]
    (cond
      (= n 0) {:id id :n n :value 1 :children []}
      (= n 1) {:id id :n n :value 1 :children []}
      :else
      (let [left (climb-tree (- n 1) counter)
            right (climb-tree (- n 2) counter)]
        {:id id :n n :value (+ (:value left) (:value right))
         :children [left right]}))))

(defn collect-tree-nodes
  "收集树所有节点（BFS序）"
  [tree]
  (loop [queue [tree] result []]
    (if (empty? queue)
      result
      (let [node (first queue)]
        (recur (concat (rest queue) (:children node))
               (conj result node))))))

(defn climb-tree->frames
  "递归调用树逐步展开 + 标注重叠子问题"
  [tree]
  (let [all-nodes (collect-tree-nodes tree)
        total (count all-nodes)
        ;; 相同n值的节点用相同颜色标注重叠
        n-colors {0 "#2ca02c" 1 "#2ca02c" 2 "#1f77b4" 3 "#ff7f0e" 4 "#d62728" 5 "#9467bd"}]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible (take step all-nodes)
              visible-ids (set (map :id visible))
              current-id (:id (last visible))]
          (graphviz/dot-template
            (str "爬楼梯 - 递归调用树 - 步骤 " step "/" total)
            (concat
              (map (fn [node]
                     (let [color (if (= (:id node) current-id)
                                  "yellow"
                                  (get n-colors (:n node) "#cccccc"))]
                       (str "n" (:id node)
                            " [label=\"climb(" (:n node) ")"
                            (when (<= (:n node) 1) (str "=" (:value node)))
                            "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                   visible)
              (mapcat (fn [node]
                        (keep (fn [child]
                                (when (visible-ids (:id child))
                                  (str "n" (:id node) " -> n" (:id child))))
                              (:children node)))
                      visible))))))))

(defn climb-dp-frames
  "DP表逐步填充帧"
  [n]
  (let [dp (vec (concat [1 1] (repeat (dec n) 0)))]
    (loop [i 2 dp dp frames []]
      (if (> i n)
        (conj frames
              (graphviz/dot-template
                (str "爬楼梯 DP表 - 完成! climb(" n ")=" (nth dp n))
                (concat
                  ["rankdir=LR"]
                  (map (fn [j]
                         (str "s" j " [label=\"dp[" j "]=" (nth dp j)
                              "\" shape=\"box\" fillcolor=\"#2ca02c\"]"))
                       (range (inc n)))
                  (map (fn [j] (str "s" j " -> s" (inc j)))
                       (range n)))))
        (let [new-val (+ (nth dp (- i 1)) (nth dp (- i 2)))
              new-dp (assoc dp i new-val)]
          (recur (inc i) new-dp
                 (conj frames
                       (graphviz/dot-template
                         (str "爬楼梯 DP表 - 填充 dp[" i "]=" new-val)
                         (concat
                           ["rankdir=LR"]
                           (map (fn [j]
                                  (let [color (cond
                                                (= j i) "yellow"
                                                (< j i) "#2ca02c"
                                                :else "#cccccc")]
                                    (str "s" j " [label=\"dp[" j "]="
                                         (if (<= j i) (nth new-dp j) "?")
                                         "\" shape=\"box\" fillcolor=\"" color "\"]")))
                                (range (inc n)))
                           (map (fn [j] (str "s" j " -> s" (inc j)))
                                (range n)))))))))))

(defn run-climbing-stairs! [n]
  (let [counter (atom 0)
        tree (climb-tree (min n 6) counter)
        tree-frames (climb-tree->frames tree)
        dp-frames (climb-dp-frames (min n 6))
        all-frames (vec (concat tree-frames dp-frames))]
    (graphviz/render-list "#graph" all-frames (atom 0))))

;; ============================================================
;; 最长公共子序列 (LCS)
;; ============================================================

(defn lcs-frames
  "LCS DP表逐步填充"
  [s1 s2]
  (let [m (count s1)
        n (count s2)
        ;; 初始化 (m+1) x (n+1) 表
        init-table (vec (repeat (inc m) (vec (repeat (inc n) 0))))]
    (loop [i 1 table init-table frames []]
      (if (> i m)
        ;; 回溯找LCS并添加最终帧
        (let [lcs-val (get-in table [m n])]
          (conj frames
                (graphviz/dot-template
                  (str "LCS(\"" s1 "\",\"" s2 "\") = " lcs-val " - 完成")
                  (concat
                    (for [ii (range (inc m)) jj (range (inc n))]
                      (str "c" ii "_" jj
                           " [label=\"" (get-in table [ii jj])
                           "\" shape=\"box\" fillcolor=\"#2ca02c\""
                           " width=\"0.5\" height=\"0.4\"]"))
                    (for [ii (range (inc m))]
                      (str "{rank=same; "
                           (clojure.string/join "; " (map #(str "c" ii "_" %) (range (inc n))))
                           "}"))
                    (for [ii (range m) jj (range (inc n))]
                      (str "c" ii "_" jj " -> c" (inc ii) "_" jj " [style=\"invis\"]"))
                    (for [ii (range (inc m)) jj (range n)]
                      (str "c" ii "_" jj " -> c" ii "_" (inc jj) " [style=\"invis\"]"))))))
        (let [[new-table new-frames]
              (loop [j 1 tbl table frms frames]
                (if (> j n)
                  [tbl frms]
                  (let [val (if (= (nth s1 (dec i)) (nth s2 (dec j)))
                              (inc (get-in tbl [(dec i) (dec j)]))
                              (max (get-in tbl [(dec i) j])
                                   (get-in tbl [i (dec j)])))
                        new-tbl (assoc-in tbl [i j] val)
                        frame (graphviz/dot-template
                                (str "LCS - 填充 [" i "," j "] "
                                     (nth s1 (dec i)) " vs " (nth s2 (dec j))
                                     " = " val)
                                (concat
                                  (for [ii (range (inc m)) jj (range (inc n))]
                                    (let [color (cond
                                                  (and (= ii i) (= jj j)) "yellow"
                                                  (or (< ii i) (and (= ii i) (< jj j))) "#2ca02c"
                                                  :else "#cccccc")]
                                      (str "c" ii "_" jj
                                           " [label=\"" (get-in new-tbl [ii jj])
                                           "\" shape=\"box\" fillcolor=\"" color "\""
                                           " width=\"0.5\" height=\"0.4\"]")))
                                  (for [ii (range (inc m))]
                                    (str "{rank=same; "
                                         (clojure.string/join "; " (map #(str "c" ii "_" %) (range (inc n))))
                                         "}"))
                                  (for [ii (range m) jj (range (inc n))]
                                    (str "c" ii "_" jj " -> c" (inc ii) "_" jj " [style=\"invis\"]"))
                                  (for [ii (range (inc m)) jj (range n)]
                                    (str "c" ii "_" jj " -> c" ii "_" (inc jj) " [style=\"invis\"]"))))]
                    (recur (inc j) new-tbl (conj frms frame)))))]
          (recur (inc i) new-table new-frames))))))

(defn run-lcs! [s1 s2]
  (let [frames (lcs-frames s1 s2)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 5)
                     lcs-s1 (reagent/atom "ABCBD")
                     lcs-s2 (reagent/atom "BDCAB")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "爬楼梯" :menu-item-name "climbing"
            :click-fn #(run-climbing-stairs! (js/parseInt @n-value))}
           {:button-name "最长公共子序列" :menu-item-name "lcs"
            :click-fn #(run-lcs! @lcs-s1 @lcs-s2)}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "爬楼梯 O(n)，LCS O(m*n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "climbing"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "楼梯数"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-climbing-stairs! (min 6 (js/parseInt @n-value)))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]
           "lcs"
           [:div.flex.flex-row {:style {:margin-top "4.5em"}}
            [:div.ml1
             [:input {:value @lcs-s1
                      :on-change #(reset! lcs-s1 (.. % -target -value))
                      :style {:width "5em"}
                      :placeholder "字符串1"}]]
            [:div.ml1
             [:input {:value @lcs-s2
                      :on-change #(reset! lcs-s2 (.. % -target -value))
                      :style {:width "5em"}
                      :placeholder "字符串2"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-lcs! @lcs-s1 @lcs-s2)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "动态规划"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

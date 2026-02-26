(ns functional-programming-visualgo-fp.algo.divide-conquer
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 二分查找 (Binary Search)
;; ============================================================

(defn binary-search-frames
  "生成二分查找的可视化帧"
  [arr target]
  (let [sorted-arr (vec (sort arr))]
    (loop [lo 0 hi (dec (count sorted-arr)) frames []]
      (if (> lo hi)
        (conj frames
              (graphviz/dot-template
                (str "二分查找 - 未找到 " target)
                (concat
                  ["rankdir=LR"]
                  (map-indexed
                    (fn [i v]
                      (str "n" i " [label=\"" v "\" shape=\"box\" fillcolor=\"#cccccc\"]"))
                    sorted-arr)
                  (map (fn [i] (str "n" i " -> n" (inc i) " [style=\"invis\"]"))
                       (range (dec (count sorted-arr)))))))
        (let [mid (quot (+ lo hi) 2)
              mid-val (nth sorted-arr mid)
              frame (graphviz/dot-template
                      (str "二分查找 target=" target " - 检查 arr[" mid "]=" mid-val)
                      (concat
                        ["rankdir=LR"]
                        (map-indexed
                          (fn [i v]
                            (let [color (cond
                                          (= i mid) "yellow"
                                          (and (>= i lo) (<= i hi)) "#1f77b4"
                                          :else "#cccccc")]
                              (str "n" i " [label=\"" v "\" shape=\"box\" fillcolor=\"" color "\"]")))
                          sorted-arr)
                        (map (fn [i] (str "n" i " -> n" (inc i) " [style=\"invis\"]"))
                             (range (dec (count sorted-arr))))))]
          (if (= mid-val target)
            ;; 找到了
            (conj frames
                  frame
                  (graphviz/dot-template
                    (str "二分查找 - 找到 " target " 在位置 " mid)
                    (concat
                      ["rankdir=LR"]
                      (map-indexed
                        (fn [i v]
                          (let [color (if (= i mid) "#2ca02c" "#cccccc")]
                            (str "n" i " [label=\"" v "\" shape=\"box\" fillcolor=\"" color "\"]")))
                        sorted-arr)
                      (map (fn [i] (str "n" i " -> n" (inc i) " [style=\"invis\"]"))
                           (range (dec (count sorted-arr)))))))
            ;; 继续搜索
            (if (< target mid-val)
              (recur lo (dec mid) (conj frames frame))
              (recur (inc mid) hi (conj frames frame)))))))))

(defn run-binary-search! [arr target]
  (let [frames (binary-search-frames arr target)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; 归并排序 (Merge Sort)
;; ============================================================

(defn merge-sort-tree
  "构建归并排序的分治树"
  [arr counter]
  (let [id (swap! counter inc)]
    (if (<= (count arr) 1)
      {:id id :arr arr :sorted arr :children []}
      (let [mid (quot (count arr) 2)
            left-arr (subvec arr 0 mid)
            right-arr (subvec arr mid)
            left (merge-sort-tree left-arr counter)
            right (merge-sort-tree right-arr counter)
            merged (vec (sort (concat (:sorted left) (:sorted right))))]
        {:id id :arr arr :sorted merged
         :children [left right]}))))

(defn collect-ms-nodes [tree]
  (loop [queue [tree] result []]
    (if (empty? queue)
      result
      (let [node (first queue)]
        (recur (concat (rest queue) (:children node))
               (conj result node))))))

(defn merge-sort-frames [tree]
  (let [all-nodes (collect-ms-nodes tree)
        total (count all-nodes)]
    (vec
      (concat
        ;; 分解阶段：逐步展开
        (for [step (range 1 (inc total))]
          (let [visible (take step all-nodes)
                visible-ids (set (map :id visible))
                current-id (:id (last visible))]
            (graphviz/dot-template
              (str "归并排序 - 分解阶段 " step "/" total)
              (concat
                (map (fn [node]
                       (let [color (cond
                                    (= (:id node) current-id) "#d62728"
                                    (empty? (:children node)) "#2ca02c"
                                    :else "#1f77b4")]
                         (str "n" (:id node)
                              " [label=\"" (clojure.string/join "," (:arr node))
                              "\" shape=\"box\" fillcolor=\"" color "\"]")))
                     visible)
                (mapcat (fn [node]
                          (keep (fn [child]
                                  (when (visible-ids (:id child))
                                    (str "n" (:id node) " -> n" (:id child))))
                                (:children node)))
                        visible)))))
        ;; 合并结果帧
        [(graphviz/dot-template
           (str "归并排序 - 完成: " (clojure.string/join "," (:sorted tree)))
           (concat
             (map (fn [node]
                    (str "n" (:id node)
                         " [label=\"" (clojure.string/join "," (:sorted node))
                         "\" shape=\"box\" fillcolor=\"#2ca02c\"]"))
                  all-nodes)
             (mapcat (fn [node]
                       (map (fn [child]
                              (str "n" (:id node) " -> n" (:id child)))
                            (:children node)))
                     all-nodes)))]))))

(defn run-merge-sort! [arr]
  (let [counter (atom 0)
        tree (merge-sort-tree arr counter)
        frames (merge-sort-frames tree)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; 快速排序 (Quick Sort)
;; ============================================================

(defn quick-sort-tree
  "构建快速排序的分治树"
  [arr counter]
  (let [id (swap! counter inc)]
    (if (<= (count arr) 1)
      {:id id :arr arr :pivot nil :children []}
      (let [pivot (first arr)
            left-arr (vec (filter #(< % pivot) (rest arr)))
            right-arr (vec (filter #(>= % pivot) (rest arr)))
            left (if (seq left-arr) (quick-sort-tree left-arr counter) nil)
            right (if (seq right-arr) (quick-sort-tree right-arr counter) nil)]
        {:id id :arr arr :pivot pivot
         :children (filterv some? [left right])}))))

(defn collect-qs-nodes [tree]
  (loop [queue [tree] result []]
    (if (empty? queue)
      result
      (let [node (first queue)]
        (recur (concat (rest queue) (:children node))
               (conj result node))))))

(defn quick-sort-frames [tree]
  (let [all-nodes (collect-qs-nodes tree)
        total (count all-nodes)]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible (take step all-nodes)
              visible-ids (set (map :id visible))
              current-id (:id (last visible))]
          (graphviz/dot-template
            (str "快速排序 - 步骤 " step "/" total)
            (concat
              (map (fn [node]
                     (let [color (cond
                                  (= (:id node) current-id) "#d62728"
                                  (empty? (:children node)) "#2ca02c"
                                  :else "#1f77b4")
                           label (str (clojure.string/join "," (:arr node))
                                      (when (:pivot node)
                                        (str "\\npivot=" (:pivot node))))]
                       (str "n" (:id node)
                            " [label=\"" label
                            "\" shape=\"box\" fillcolor=\"" color "\"]")))
                   visible)
              (mapcat (fn [node]
                        (keep (fn [child]
                                (when (visible-ids (:id child))
                                  (str "n" (:id node) " -> n" (:id child))))
                              (:children node)))
                      visible))))))))

(defn run-quick-sort! [arr]
  (let [counter (atom 0)
        tree (quick-sort-tree arr counter)
        frames (quick-sort-frames tree)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; 汉诺塔 (Tower of Hanoi)
;; ============================================================

(defn hanoi-moves
  "生成汉诺塔移动序列"
  [n from to aux]
  (if (= n 1)
    [{:disk 1 :from from :to to}]
    (concat
      (hanoi-moves (dec n) from aux to)
      [{:disk n :from from :to to}]
      (hanoi-moves (dec n) aux to from))))

(defn hanoi-frames
  "汉诺塔可视化帧"
  [n]
  (let [moves (hanoi-moves n "A" "C" "B")
        disk-names (mapv #(str "d" %) (range 1 (inc n)))
        ;; 初始状态：所有盘子在A柱
        init-state {"A" (vec (range 1 (inc n))) "B" [] "C" []}
        disk-label (fn [d] (apply str (repeat (- (* 2 n) (* 2 d) -3) "=")))]
    (loop [remaining moves
           state init-state
           step 0
           frames [(graphviz/dot-template
                     (str "汉诺塔 " n " 个盘子 - 初始状态")
                     (concat
                       (for [d (range 1 (inc n))]
                         (str "d" d " [label=\"" (disk-label d)
                              "\" shape=\"box\" fillcolor=\""
                              (nth ["#d62728" "#1f77b4" "#2ca02c" "#ff7f0e" "#9467bd"] (dec d))
                              "\"]"))
                       [(str "subgraph cluster_A { label=\"柱A\"; "
                             (clojure.string/join "; " (map #(str "d" %) (get init-state "A")))
                             " }")
                        (str "subgraph cluster_B { label=\"柱B\"; empty_B [label=\"\" style=\"invis\"] }")
                        (str "subgraph cluster_C { label=\"柱C\"; empty_C [label=\"\" style=\"invis\"] }")]))]]
      (if (empty? remaining)
        frames
        (let [{:keys [disk from to]} (first remaining)
              ;; 从 from 移除 disk，添加到 to
              new-state (-> state
                            (update from (fn [v] (vec (remove #{disk} v))))
                            (update to conj disk))
              frame (graphviz/dot-template
                      (str "汉诺塔 - 步骤 " (inc step) ": 盘" disk " " from "->" to)
                      (concat
                        (for [d (range 1 (inc n))]
                          (str "d" d " [label=\"" (disk-label d)
                               "\" shape=\"box\" fillcolor=\""
                               (if (= d disk) "yellow"
                                   (nth ["#d62728" "#1f77b4" "#2ca02c" "#ff7f0e" "#9467bd"] (dec d)))
                               "\"]"))
                        (for [peg ["A" "B" "C"]]
                          (let [disks (get new-state peg)]
                            (str "subgraph cluster_" peg " { label=\"柱" peg "\"; "
                                 (if (empty? disks)
                                   (str "empty_" peg " [label=\"\" style=\"invis\"]")
                                   (clojure.string/join "; " (map #(str "d" %) disks)))
                                 " }")))))]
          (recur (rest remaining) new-state (inc step) (conj frames frame)))))))

(defn run-hanoi! [n]
  (let [frames (hanoi-frames (min n 4))]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [search-target (reagent/atom 7)
                     sort-data (reagent/atom "38,27,43,3,9,82,10")
                     hanoi-n (reagent/atom 3)]
    (let [parse-arr (fn [s] (vec (map js/parseInt (clojure.string/split s #","))))
          left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "二分查找" :menu-item-name "binary-search" :click-fn nil}
           {:button-name "归并排序" :menu-item-name "merge-sort" :click-fn nil}
           {:button-name "快速排序" :menu-item-name "quick-sort" :click-fn nil}
           {:button-name "汉诺塔" :menu-item-name "hanoi" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "二分查找O(logn) 归并O(nlogn) 快排O(nlogn) 汉诺塔O(2^n)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "binary-search"
           [:div.flex.flex-column {:style {:margin-top "2.5em"}}
            [:div.flex.flex-row.ml1
             [:div.f6 "数组:"]
             [:input {:value @sort-data
                      :on-change #(reset! sort-data (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组"}]]
            [:div.flex.flex-row.ml1.mt1
             [:div.f6 "目标:"]
             [:input {:value @search-target
                      :on-change #(reset! search-target (.. % -target -value))
                      :style {:width "4em"}
                      :type "number"}]]
            [:div.bg-yellow.ml1.mt1.pa1.f6
             {:on-click #(run-binary-search! (parse-arr @sort-data) (js/parseInt @search-target))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "搜索"]]
           "merge-sort"
           [:div.flex.flex-row {:style {:margin-top "4.5em"}}
            [:div.ml1
             [:input {:value @sort-data
                      :on-change #(reset! sort-data (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-merge-sort! (parse-arr @sort-data))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "排序"]]
           "quick-sort"
           [:div.flex.flex-row {:style {:margin-top "6.5em"}}
            [:div.ml1
             [:input {:value @sort-data
                      :on-change #(reset! sort-data (.. % -target -value))
                      :style {:width "10em"}
                      :placeholder "数组"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-quick-sort! (parse-arr @sort-data))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "排序"]]
           "hanoi"
           [:div.flex.flex-row {:style {:margin-top "8.5em"}}
            [:div.ml1
             [:input {:value @hanoi-n
                      :on-change #(reset! hanoi-n (.. % -target -value))
                      :style {:width "4em"}
                      :type "number"
                      :placeholder "盘数"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-hanoi! (min 4 (js/parseInt @hanoi-n)))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "分治算法"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

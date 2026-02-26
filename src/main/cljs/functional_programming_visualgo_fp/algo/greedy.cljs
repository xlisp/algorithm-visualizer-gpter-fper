(ns functional-programming-visualgo-fp.algo.greedy
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 活动选择问题 (Activity Selection)
;; ============================================================

(def default-activities
  [{:id "A" :start 1 :end 4}
   {:id "B" :start 3 :end 5}
   {:id "C" :start 0 :end 6}
   {:id "D" :start 5 :end 7}
   {:id "E" :start 3 :end 9}
   {:id "F" :start 5 :end 9}
   {:id "G" :start 6 :end 10}
   {:id "H" :start 8 :end 11}
   {:id "I" :start 8 :end 12}
   {:id "J" :start 2 :end 14}
   {:id "K" :start 12 :end 16}])

(defn activity-selection
  "贪心选择：按结束时间排序，逐步选择不冲突的活动"
  [activities]
  (let [sorted (sort-by :end activities)]
    (loop [remaining (rest sorted)
           selected [(first sorted)]
           last-end (:end (first sorted))
           steps [{:selected [(first sorted)] :considering nil :rejected []}]]
      (if (empty? remaining)
        steps
        (let [current (first remaining)
              compatible? (>= (:start current) last-end)]
          (if compatible?
            (recur (rest remaining)
                   (conj selected current)
                   (:end current)
                   (conj steps {:selected (conj selected current)
                                :considering current
                                :rejected []}))
            (recur (rest remaining)
                   selected
                   last-end
                   (conj steps {:selected selected
                                :considering current
                                :rejected [current]}))))))))

(defn activity-steps->dots [activities steps]
  (let [sorted (sort-by :end activities)]
    (vec
      (map-indexed
        (fn [idx step]
          (let [selected-ids (set (map :id (:selected step)))
                considering (:considering step)
                considering-id (when considering (:id considering))]
            (graphviz/dot-template
              (str "活动选择问题 - 步骤 " (inc idx) "/" (count steps))
              (concat
                ["rankdir=LR"]
                (map (fn [act]
                       (let [color (cond
                                    (= (:id act) considering-id) "yellow"
                                    (selected-ids (:id act)) "#2ca02c"
                                    :else "#1f77b4")]
                         (str (:id act)
                              " [label=\"" (:id act) " [" (:start act) "," (:end act) "]\""
                              " shape=\"box\" fillcolor=\"" color "\"]")))
                     sorted)))))
        steps))))

(defn run-activity-selection! []
  (let [steps (activity-selection default-activities)
        dots (activity-steps->dots default-activities steps)]
    (graphviz/render-list "#graph" dots (atom 0))))

;; ============================================================
;; Huffman 编码树
;; ============================================================

(def default-huffman-data
  [["a" 5] ["b" 9] ["c" 12] ["d" 13] ["e" 16] ["f" 45]])

(defn huffman-build-steps
  "构建Huffman树，记录每步合并过程"
  [char-freqs]
  (let [initial-nodes (mapv (fn [[ch freq]] {:id ch :label (str ch ":" freq) :freq freq :children []}) char-freqs)]
    (loop [nodes (vec (sort-by :freq initial-nodes))
           steps [{:nodes nodes :merged nil}]
           merge-id 0]
      (if (<= (count nodes) 1)
        {:steps steps :tree (first nodes)}
        (let [n1 (first nodes)
              n2 (second nodes)
              new-node {:id (str "m" merge-id)
                        :label (str (+ (:freq n1) (:freq n2)))
                        :freq (+ (:freq n1) (:freq n2))
                        :children [n1 n2]}
              remaining (vec (sort-by :freq (conj (subvec nodes 2) new-node)))]
          (recur remaining
                 (conj steps {:nodes remaining :merged new-node})
                 (inc merge-id)))))))

(defn huffman-node->dot-lines
  "递归生成Huffman树节点的DOT行"
  [node]
  (let [node-def (str (:id node)
                      " [label=\"" (:label node) "\""
                      " shape=\"circle\""
                      " fillcolor=\"" (if (empty? (:children node)) "#1f77b4" "#ff7f0e") "\"]")]
    (concat
      [node-def]
      (map-indexed
        (fn [idx child]
          (str (:id node) " -> " (:id child) " [label=\"" idx "\"]"))
        (:children node))
      (mapcat huffman-node->dot-lines (:children node)))))

(defn huffman-steps->dots [steps]
  (vec
    (map-indexed
      (fn [idx step]
        (graphviz/dot-template
          (str "Huffman编码树 - 步骤 " (inc idx) "/" (count steps))
          (mapcat huffman-node->dot-lines (:nodes step))))
      steps)))

(defn run-huffman! []
  (let [{:keys [steps tree]} (huffman-build-steps default-huffman-data)
        dots (huffman-steps->dots steps)]
    (graphviz/render-list "#graph" dots (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "活动选择" :menu-item-name "activity"
          :click-fn #(run-activity-selection!)}
         {:button-name "Huffman编码" :menu-item-name "huffman"
          :click-fn #(run-huffman!)}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
          :click-fn #(js/alert "活动选择 O(n log n)，Huffman O(n log n)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "activity" [:div]
         "huffman" [:div]}]
    (comps/base-page
      :title "贪心算法"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

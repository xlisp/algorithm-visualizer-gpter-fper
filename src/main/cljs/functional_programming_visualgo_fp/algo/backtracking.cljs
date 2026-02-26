(ns functional-programming-visualgo-fp.algo.backtracking
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [functional-programming-visualgo-fp.scheme :as sch]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 八皇后问题
;; ============================================================

(defn safe?
  "检查在 (row, col) 放置皇后是否安全"
  [queens row col]
  (every?
    (fn [[r c]]
      (and (not= c col)
           (not= (Math/abs (- r row)) (Math/abs (- c col)))))
    queens))

(defn solve-queens
  "回溯法解八皇后，记录搜索过程"
  [n]
  (let [steps (atom [])
        counter (atom 0)]
    (letfn [(solve [row queens path-id]
              (if (= row n)
                (swap! steps conj {:type :solution :queens queens :id path-id})
                (doseq [col (range n)]
                  (let [node-id (swap! counter inc)]
                    (if (safe? queens row col)
                      (do
                        (swap! steps conj {:type :place :row row :col col
                                           :queens (conj queens [row col])
                                           :id node-id :parent-id path-id})
                        (solve (inc row) (conj queens [row col]) node-id))
                      (swap! steps conj {:type :conflict :row row :col col
                                         :queens queens
                                         :id node-id :parent-id path-id}))))))]
      (solve 0 [] 0))
    @steps))

(defn queens-steps->dots
  "将八皇后搜索步骤转为DOT帧（取前50步避免过多帧）"
  [steps n]
  (let [limited-steps (take 50 steps)]
    (vec
      (for [end-idx (range 1 (inc (count limited-steps)))]
        (let [visible (take end-idx limited-steps)]
          (graphviz/dot-template
            (str "八皇后问题 " n "x" n " - 步骤 " end-idx "/" (count limited-steps))
            (concat
              (map (fn [step]
                     (let [color (case (:type step)
                                  :place "#2ca02c"
                                  :conflict "#d62728"
                                  :solution "#ff7f0e"
                                  "#1f77b4")
                           label (case (:type step)
                                   :place (str "R" (:row step) "C" (:col step))
                                   :conflict (str "R" (:row step) "C" (:col step) " X")
                                   :solution "Solution!"
                                   "")]
                       (str "n" (:id step)
                            " [label=\"" label "\""
                            " shape=\"box\" fillcolor=\"" color "\"]")))
                   visible)
              (keep (fn [step]
                      (when (and (:parent-id step) (pos? (:parent-id step)))
                        (str "n" (:parent-id step) " -> n" (:id step))))
                    visible))))))))

(defn run-queens! [n]
  (let [steps (solve-queens (min n 6))
        dots (queens-steps->dots steps (min n 6))]
    (graphviz/render-list "#graph" dots (atom 0))))

;; ============================================================
;; 全排列问题
;; ============================================================

(defn permutation-tree
  "构建全排列搜索树"
  [elements]
  (let [counter (atom 0)]
    (letfn [(build [remaining chosen]
              (let [id (swap! counter inc)]
                {:id id
                 :label (str chosen)
                 :children
                 (if (empty? remaining)
                   []
                   (mapv (fn [elem]
                           (build (remove #{elem} remaining)
                                  (conj chosen elem)))
                         remaining))}))]
      (build elements []))))

(defn perm-tree->dot-frames
  "将全排列搜索树逐步展开为DOT帧"
  [tree]
  (let [all-nodes (atom [])
        _ (letfn [(collect [node]
                    (swap! all-nodes conj node)
                    (doseq [child (:children node)]
                      (collect child)))]
            (collect tree))
        nodes @all-nodes
        total (count nodes)]
    (vec
      (for [step (range 1 (inc total))]
        (let [visible (take step nodes)
              visible-ids (set (map :id visible))
              current-id (:id (last visible))
              leaf-ids (set (map :id (filter #(empty? (:children %)) visible)))]
          (graphviz/dot-template
            (str "全排列搜索树 - 步骤 " step "/" total)
            (concat
              (map (fn [node]
                     (let [color (cond
                                  (= (:id node) current-id) "#d62728"
                                  (leaf-ids (:id node)) "#2ca02c"
                                  :else "#1f77b4")]
                       (str "n" (:id node)
                            " [label=\"" (:label node) "\""
                            " shape=\"box\" fillcolor=\"" color "\"]")))
                   visible)
              (mapcat (fn [node]
                        (keep (fn [child]
                                (when (visible-ids (:id child))
                                  (str "n" (:id node) " -> n" (:id child))))
                              (:children node)))
                      visible))))))))

(defn run-permutation! [n]
  (let [elements (vec (range 1 (inc (min n 4))))
        tree (permutation-tree elements)
        frames (perm-tree->dot-frames tree)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 4)]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "八皇后" :menu-item-name "queens" :click-fn nil}
           {:button-name "全排列" :menu-item-name "permutation" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "八皇后 O(n!)，全排列 O(n!)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "queens"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-queens! 6)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]
           "permutation"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "4em"}
                      :placeholder "n值"
                      :type "number"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-permutation! (min 4 (js/parseInt @n-value)))
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "回溯算法"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

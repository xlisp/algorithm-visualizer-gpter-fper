(ns functional-programming-visualgo-fp.algo.suffix-tree
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 后缀树（简化版Trie）可视化
;; ============================================================

(defn build-suffix-trie
  "构建后缀Trie，返回节点和边"
  [s]
  (let [counter (atom 0)
        root-id (swap! counter inc)
        nodes (atom {root-id {:id root-id :label "root"}})
        edges (atom [])]
    (doseq [start (range (count s))]
      (let [suffix (subs s start)]
        (loop [current root-id
               i 0]
          (when (< i (count suffix))
            (let [ch (str (nth suffix i))
                  ;; 查找是否有现成的子边
                  existing (first (filter (fn [e]
                                           (and (= (:from e) current)
                                                (= (:label e) ch)))
                                         @edges))]
              (if existing
                (recur (:to existing) (inc i))
                (let [new-id (swap! counter inc)]
                  (swap! nodes assoc new-id {:id new-id :label ch})
                  (swap! edges conj {:from current :to new-id :label ch})
                  (recur new-id (inc i)))))))))
    {:nodes @nodes :edges @edges}))

(defn suffix-trie->dot-frames
  "逐步构建后缀Trie"
  [s]
  (let [counter (atom 0)
        root-id (swap! counter inc)
        nodes (atom {root-id {:id root-id :label "root"}})
        edges (atom [])
        frames (atom [])]
    (doseq [start (range (count s))]
      (let [suffix (subs s start)]
        (loop [current root-id
               i 0]
          (when (< i (count suffix))
            (let [ch (str (nth suffix i))
                  existing (first (filter (fn [e]
                                           (and (= (:from e) current)
                                                (= (:label e) ch)))
                                         @edges))]
              (if existing
                (recur (:to existing) (inc i))
                (let [new-id (swap! counter inc)]
                  (swap! nodes assoc new-id {:id new-id :label ch})
                  (swap! edges conj {:from current :to new-id :label ch})
                  (swap! frames conj
                         (graphviz/dot-template
                           (str "后缀Trie - 插入后缀 \"" suffix "\" 字符'" ch "'")
                           (concat
                             (map (fn [[id node]]
                                    (let [color (if (= id new-id) "#d62728"
                                                   (if (= id root-id) "#2ca02c" "#1f77b4"))]
                                      (str "n" id " [label=\"" (:label node)
                                           "\" shape=\"circle\" fillcolor=\"" color "\"]")))
                                  @nodes)
                             (map (fn [e]
                                    (str "n" (:from e) " -> n" (:to e)
                                         " [label=\"" (:label e) "\"]"))
                                  @edges))))
                  (recur new-id (inc i)))))))))
    @frames))

(defn run-suffix-tree! [s]
  (let [text (str s "$")
        frames (suffix-trie->dot-frames text)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [text-value (reagent/atom "banana")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "构建" :menu-item-name "build" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "后缀Trie构建 O(n^2)")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "build"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @text-value
                      :on-change #(reset! text-value (.. % -target -value))
                      :style {:width "8em"}
                      :placeholder "字符串"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-suffix-tree! @text-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "构建"]]}]
      (comps/base-page
        :title "后缀树 (Suffix Trie)"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

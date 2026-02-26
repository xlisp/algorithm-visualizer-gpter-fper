(ns functional-programming-visualgo-fp.algo.union-find
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.components :as comps]
            [herb.core :refer [<class]]
            [functional-programming-visualgo-fp.multiplexing-css :as css]))

;; ============================================================
;; 并查集可视化
;; ============================================================

(defn make-uf [n]
  {:parent (vec (range n))
   :rank (vec (repeat n 0))})

(defn find-root [parent x]
  (if (= (nth parent x) x)
    x
    (recur parent (nth parent x))))

(defn union-step [uf x y]
  (let [rx (find-root (:parent uf) x)
        ry (find-root (:parent uf) y)]
    (if (= rx ry)
      uf
      (let [rank (:rank uf)]
        (cond
          (< (nth rank rx) (nth rank ry))
          (assoc uf :parent (assoc (:parent uf) rx ry))
          (> (nth rank rx) (nth rank ry))
          (assoc uf :parent (assoc (:parent uf) ry rx))
          :else
          (-> uf
              (assoc :parent (assoc (:parent uf) ry rx))
              (assoc :rank (assoc rank rx (inc (nth rank rx))))))))))

(defn uf->dot [uf label highlight-x highlight-y]
  (let [parent (:parent uf)
        n (count parent)]
    (graphviz/dot-template
      label
      (concat
        (map (fn [i]
               (let [color (cond
                             (= i highlight-x) "#d62728"
                             (= i highlight-y) "#ff7f0e"
                             (= (nth parent i) i) "#2ca02c"
                             :else "#1f77b4")]
                 (str "n" i " [label=\"" i "\" shape=\"circle\" fillcolor=\"" color "\"]")))
             (range n))
        (keep (fn [i]
                (when (not= (nth parent i) i)
                  (str "n" i " -> n" (nth parent i))))
              (range n))))))

(defn union-frames
  "执行一系列union操作，生成可视化帧"
  [n pairs]
  (let [frames (atom [])
        initial-uf (make-uf n)]
    (swap! frames conj (uf->dot initial-uf "并查集 - 初始状态" -1 -1))
    (loop [uf initial-uf
           remaining pairs
           step 1]
      (if (empty? remaining)
        @frames
        (let [[x y] (first remaining)
              new-uf (union-step uf x y)]
          (swap! frames conj
                 (uf->dot new-uf (str "并查集 - Union(" x "," y ") 步骤" step) x y))
          (recur new-uf (rest remaining) (inc step)))))))

(defn run-union-find! [n pairs-str]
  (let [pairs (mapv (fn [p]
                      (let [parts (clojure.string/split p #"-")]
                        [(js/parseInt (first parts)) (js/parseInt (second parts))]))
                    (clojure.string/split pairs-str #","))
        frames (union-frames n pairs)]
    (graphviz/render-list "#graph" frames (atom 0))))

;; ============================================================
;; Page 组件
;; ============================================================

(defn page []
  (reagent/with-let [n-value (reagent/atom 8)
                     pairs-value (reagent/atom "0-1,2-3,4-5,6-7,0-2,4-6,0-4")]
    (let [left-menu-datas
          [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
           {:button-name "合并操作" :menu-item-name "union" :click-fn nil}
           {:button-name "算法时间复杂度" :menu-item-name "time-complexity"
            :click-fn #(js/alert "并查集 Union/Find 近似 O(α(n))")}]
          left-menu-item-datas
          {"graphviz" [:div]
           "union"
           [:div.flex.flex-row {:style {:margin-top "2.5em"}}
            [:div.ml1
             [:input {:value @n-value
                      :on-change #(reset! n-value (.. % -target -value))
                      :style {:width "3em"}
                      :placeholder "节点数"
                      :type "number"}]]
            [:div.ml1
             [:input {:value @pairs-value
                      :on-change #(reset! pairs-value (.. % -target -value))
                      :style {:width "12em"}
                      :placeholder "合并对(如0-1,2-3)"}]]
            [:div.bg-yellow.ml1.pa1.f6
             {:on-click #(run-union-find! (js/parseInt @n-value) @pairs-value)
              :class (<class css/hover-menu-style)
              :style {:width "4em"}} "运行"]]}]
      (comps/base-page
        :title "并查集"
        :left-menu-datas left-menu-datas
        :left-menu-item-datas left-menu-item-datas))))

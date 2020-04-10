(ns functional-programming-visualgo-fp.svg.helper
  (:require
   [reagent.core :as reagent]))

(comment
  ;; 创建一个红色的斜线,SVG每一个元素都是一个element:
  (create-svg-ele "line" {:x1 50 :y1 50 :x2 350 :stroke "red" :stroke-width 2})

  [:div
   [:svg {:id "mysvg"
          :width 640
          :height 480
          :style {:border 1
                  :solid "#000000"}}]]

  (let [svg (.getElementById js/document "mysvg")
        line-ele (create-svg-ele
                   "line"
                   {:x1 50 :y1 50 :x2 350 :stroke "red" :stroke-width 2})
        circle-ele (create-svg-ele ;; 创建一个黄色的圆球
                     "circle"
                     {:cx 200 :cy 50 :r 50 :fill "#ffcc00"})
        rect-ele (create-svg-ele ;;创建一个蓝色的矩形
                   "rect"
                   {:x 150 :y 100 :width 100 :height 100 :fill "blue"})
        path-ele (create-svg-ele ;;创建了一个三角形: 任意多边形或者是路径
                   "path"
                   {:d "M600 360 L620 380 L600 400 Z"
                    :stroke "black"
                    :stroke-width 1})
        ;; ----- 创建一个"如果"的块
        path1-ele (create-svg-ele ;; 创建一个单个的blockly,单行向下结合的block
                    "path"
                    {:d "m 0,8 A 8,8 0 0,1 8,0 H 49.329986572265625 v 24 H 29.5 l -6,4 -3,0 -6,-4 H 8 a 8,8 0 0,1 -8,-8 z"
                     :transform "translate(1,1)"
                     :fill "#496684"})
        text-ele (create-svg-ele
                   "text"
                   {:y "12.5"
                    :transform "translate(10,5)"})
        _ (set! (.-textContent text-ele) "如果")]
    (doseq [ele [line-ele circle-ele rect-ele path-ele path1-ele text-ele]]
      (.appendChild svg ele))))
(defn create-svg-ele [tag-name atrrs]
  (let [ele (.createElementNS js/document "http://www.w3.org/2000/svg" tag-name)]
    (doseq [item atrrs]
      (.setAttribute ele (name (first item)) (str (last item))))
    ele))

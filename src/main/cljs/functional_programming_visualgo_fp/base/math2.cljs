(ns functional-programming-visualgo-fp.base.math2
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [functional-programming-visualgo-fp.graphviz :as graphviz]
            [functional-programming-visualgo-fp.panel :as panel]
            [functional-programming-visualgo-fp.datas :as datas]
            [functional-programming-visualgo-fp.components :as comps]))


(defn some-coin [kinds-of-coins]
  (let [coin (cond
               ;; 总共有五种找零的方式: kinds-of-coins是一直递减的
               (= kinds-of-coins 5) 50
               (= kinds-of-coins 4) 25
               (= kinds-of-coins 3) 10
               (= kinds-of-coins 2) 5
               (= kinds-of-coins 1) 1) ]
    (prn ;; "------ 类别: " kinds-of-coins
      " 零钱面值" coin)
    coin))

(comment
  ;; count_change（amount，n）=count_change（amount，n-1）+count_change(amount-amount_of_first_coin,n)

  (count-change 5 5)  ;; 5 * 1, 1 * 5
  ;; => 2 ;; amount为0的次数出现了两次
  (count-change 10 5) ;;=> 4种
  ;; amount为0的次数出现了4次
  ;; 10 * 1, 5 * 2, 1 * 10, 5 * 1 + 1 * 5
  ;; 如何解释 `5 * 1 + 1 * 5` 是关键! => 数字是高阶函数的观点
  )
(defn count-change
  [amount kinds-of-coins]
  ;; (prn amount "=====" kinds-of-coins)
  (if (= amount 0)
    (prn "amount为0, 零钱为: " (some-coin kinds-of-coins) ", 计数器加一"))
  (cond
    ;; 一切都是高阶函数, 包括字符串和数字(邱奇数的观点): 把amount当成一个高阶函数, 金额5的阶数和金额10的阶数是不同的, 金额10的一定量衰减就是金额5 => 当前的高阶函数数字 和 前一个高阶函数数字的关系是什么?(数学归纳法)
    (= amount 0) 1 ;; 只有金额衰减为了0,才能够计数器加一
    (or (< amount 0) (= kinds-of-coins 0)) 0 ;; 如果种类为0,但是金额衰减不为0,就只能
    :else
    (+ ;; + 为累加, 计数器,把 1, 0 , 0 , 1...加起来
      ;; 谁放在+的第一位就谁第一次全部递归展开,因为它是优先计算的(表达式在+号的第一位)
      (let [amount-dec (- amount (some-coin kinds-of-coins)) ]
        (prn "金额衰减: " amount " -> " amount-dec)
        (count-change amount-dec kinds-of-coins))
      ;;
      (let [coin-dec (- kinds-of-coins 1)]
        (prn "Start ===== 类别衰减: " coin-dec)
        (count-change amount coin-dec))
      )))

(defn page []
  (let [left-menu-datas
        [{:button-name "GraphViz图" :menu-item-name "graphviz" :click-fn nil}
         {:button-name "可视化计算过程" :menu-item-name "visual-process"
          :click-fn #(js/alert (str "10元,用五种钱来找零,1,5,10,25,50, 共有" (count-change 10 5) "种找零方式: 10 * 1, 5 * 2, 1 * 10, 5 * 1 + 1 * 5" ))}
         {:button-name "算法时间复杂度" :menu-item-name "time-complexity" :click-fn  #(js/alert "算法时间复杂度为O(n^m)")}]
        left-menu-item-datas
        {"graphviz" [:div]
         "visual-process" [:div]}]
    (comps/base-page
      :title "找零钱问题"
      :left-menu-datas left-menu-datas
      :left-menu-item-datas left-menu-item-datas)))

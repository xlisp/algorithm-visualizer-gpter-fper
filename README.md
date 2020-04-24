# 函数式算法可视化 functional programming algorithm visualizer

* 太极来描述递归的对立统一的两面: eval,apply元解释器λ演算解释一切复杂算法
![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/太极来描述递归的对立统一的两面SICP_EVAL_APPLY_元解释器.png)



- [函数式算法可视化 functional programming algorithm visualizer](#%E5%87%BD%E6%95%B0%E5%BC%8F%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96-functional-programming-algorithm-visualizer)
  - [算法可视化开发第一性原则](#%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96%E5%BC%80%E5%8F%91%E7%AC%AC%E4%B8%80%E6%80%A7%E5%8E%9F%E5%88%99)
    - [递归方法的总结](#%E9%80%92%E5%BD%92%E6%96%B9%E6%B3%95%E7%9A%84%E6%80%BB%E7%BB%93)
  - [算法可视化导航](#%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96%E5%AF%BC%E8%88%AA)
  - [如何在数组中找到直角三角形的组合？(map-reduce-filter式解法)](#%E5%A6%82%E4%BD%95%E5%9C%A8%E6%95%B0%E7%BB%84%E4%B8%AD%E6%89%BE%E5%88%B0%E7%9B%B4%E8%A7%92%E4%B8%89%E8%A7%92%E5%BD%A2%E7%9A%84%E7%BB%84%E5%90%88map-reduce-filter%E5%BC%8F%E8%A7%A3%E6%B3%95)
  - [二叉搜索树](#%E4%BA%8C%E5%8F%89%E6%90%9C%E7%B4%A2%E6%A0%91)
  - [SICP找零钱问题(递推式递归解法)](#sicp%E6%89%BE%E9%9B%B6%E9%92%B1%E9%97%AE%E9%A2%98%E9%80%92%E6%8E%A8%E5%BC%8F%E9%80%92%E5%BD%92%E8%A7%A3%E6%B3%95)

##  算法可视化开发第一性原则
* 用二维的表格或者矩阵存储数据(低维度的基本结构), 展示为高维的图或者树形结构, 而不要去存储高维结构,然后去解析高维结构生成高维结构
* 充分利用数据库或者atom来存储中间过程, 然后API传给前端展示出来
* 可视化前端驱动Repl lambda演算开发算法: 编写可视化的工具函数群,来快速可视化一个新的算法过程
* 高阶函数描述复杂过程`(fn ... (f2 (f1 x)))` => 递归脚手架: 找到递归(递推的通项公式)终止条件(不同的算法递归描述的终止条件都不一样) => 去递归 => 去for循环,向量化
* 先写死展示的数据,然后可视化开发结束(遵从易道),然后"吃饱"之后,最后变成灵活的自定义生成的数据,能够让人代入自身的易于理解的数据进去运行: 不要一上来就想着开发很完美的可视化过程,直接开发高维结构到高维结构的映射
* 算法可视化开发如同吃饭天天吃,每天不断接近目标的可视化效果,直到最后能够随意代入自身数据进去运行,直接展示该算法的物理或现实意义: 刚开始从一个GraphViz图描述算法开始,慢慢细化,直到最后全部开发完一个算法的可视化过程
* 先很粗暴的算法实现功能(暴力Repl人肉拟合未知函数), 然后对照旧的代码重写一遍
* 代数进去算法得到局部打印的数列: 用等差和等比数列来表示所有数据的规律, 离散数据的规律, 发现数列的规律找到通项公式 或者是递推公式
* 数学归纳法思想(有名(不同的算法的名字)万物(所有软件)之始,无名(数学归纳证明法)万物之母): 递归版本是最容易的, 运用万能的数学归纳法来证明所有复杂的公式定理，就算你完全不知道一个算法的名字，知道需求输入输出的样子，都能用递归描述出来 => 然后是非递归版本,用栈实现
* 一切都是高阶函数, 包括字符串和数字(邱奇数的观点): 把amount当成一个高阶函数, 金额5的阶数和金额10的阶数是不同的, 金额10的一定量衰减就是金额5 => 当前的高阶函数数字 和 前一个高阶函数数字的关系是什么?(数学归纳法)
* emacs yasnippad递归多分支的脚手架帮助开发算法
* 打印prn信息来文学编程: 写成你能理解的方式就行 => log文学编程化

### 递归方法的总结

* 找到不可变的定点,即最基本的底维度的结构,衰减问题不可变的规律: 先降维度最简描述定点(最基本的简单结构), 然后升维度(递归低维,高维结果展示出来)
* 找到规律来缩小这个问题的搜索空间: 确定函数的定义域和值域的边界在哪里
* 分段函数来cond求和结果
* 尝试用代数穷举小的数字,来描述衰减前者和衰减后者的关系,如何消解问题: count_change（amount，n）, count_change（amount，n-1）, count_change(amount-衰减的钱,n) 抽取出来的因子的关系
* 用递归脚手架来爆栈来尝试衰减你的目标变量
* 平时独立思考和编码练习: 从几个衰减变量中找到前后者关系 => 从一个递推公式直接到递归脚手架写出代码
* 用公式来表达多个情况相加, 然后排出不可能的组合方式(A1 = A0 + A(?)): count_change(amount，n) = count_change(amount，n-1) + count_change(amount-amount_of_first_coin,n)

##  算法可视化导航

![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/website_preview.png)

##  如何在数组中找到直角三角形的组合？(map-reduce-filter式解法)

![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/demo_fp_visualgo.gif)

##  二叉搜索树
![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/demo_bst_search.gif)

##  SICP找零钱问题(递推式递归解法)

![]()

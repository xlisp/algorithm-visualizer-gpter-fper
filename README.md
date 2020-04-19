# 函数式算法可视化 functional programming algorithm visualizer

- [函数式算法可视化 functional programming algorithm visualizer](#%E5%87%BD%E6%95%B0%E5%BC%8F%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96-functional-programming-algorithm-visualizer)
  - [算法可视化开发第一性原则](#%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96%E5%BC%80%E5%8F%91%E7%AC%AC%E4%B8%80%E6%80%A7%E5%8E%9F%E5%88%99)
  - [算法可视化导航](#%E7%AE%97%E6%B3%95%E5%8F%AF%E8%A7%86%E5%8C%96%E5%AF%BC%E8%88%AA)
  - [如何在数组中找到直角三角形的组合？](#%E5%A6%82%E4%BD%95%E5%9C%A8%E6%95%B0%E7%BB%84%E4%B8%AD%E6%89%BE%E5%88%B0%E7%9B%B4%E8%A7%92%E4%B8%89%E8%A7%92%E5%BD%A2%E7%9A%84%E7%BB%84%E5%90%88)
  - [二叉搜索树](#%E4%BA%8C%E5%8F%89%E6%90%9C%E7%B4%A2%E6%A0%91)

##  算法可视化开发第一性原则
* 用二维的表格或者矩阵存储数据(低维度的基本结构), 展示为高维的图或者树形结构, 而不要去存储高维结构,然后去解析高维结构生成高维结构
* 充分利用数据库来存储中间过程, 然后API传给前端展示出来
* 可视化前端驱动Repl lambda演算开发算法: 编写可视化的工具函数群,来快速可视化一个新的算法过程
* 高阶函数描述复杂过程`(fn ... (f2 (f1 x)))` => 递归脚手架: 找到递归(递推的通项公式)终止条件 => 去递归 => 去for循环,向量化
* 先写死展示的数据,然后可视化开发结束(遵从易道),然后"吃饱"之后,最后变成灵活的自定义生成的数据,能够让人代入自身的易于理解的数据进去运行: 不要一上来就想着开发很完美的可视化过程,直接开发高维结构到高维结构的映射
* 算法可视化开发如同吃饭天天吃,每天不断接近目标的可视化效果,直到最后能够随意代入自身数据进去运行,直接展示该算法的物理或现实意义: 刚开始从一个GraphViz图描述算法开始,慢慢细化,直到最后全部开发完一个算法的可视化过程
* 先很粗暴的算法实现功能(暴力Repl人肉拟合未知函数), 然后对照旧的代码重写一遍
* 代数进去算法得到局部打印的数列: 用等差和等比数列来表示所有数据的规律, 离散数据的规律, 发现数列的规律找到通项公式 或者是递推公式

##  算法可视化导航

![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/website_preview.png)

##  如何在数组中找到直角三角形的组合？

![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/demo_fp_visualgo.gif)

##  二叉搜索树
![](https://raw.githubusercontent.com/chanshunli/functional-programming-visualgo/master/demo_bst_search.gif)

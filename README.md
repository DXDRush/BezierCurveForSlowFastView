# BezierCurveForSlowFastView
using bezier curve to implement a function like a video editing app called Slow-Fast

1.效果

<img src="https://github.com/DXDRush/BezierCurveForSlowFastView/blob/master/screenshots/1.png" width = "405" height = "720" />
<img src="https://github.com/DXDRush/BezierCurveForSlowFastView/blob/master/screenshots/2.gif" width = "405" height = "720" />

2.原理
       利用三阶贝塞尔曲线
       以每两个控制点为起始，终止节点
       在起始终止节点范围内找到三个构造贝塞尔曲线的数据点
       用cubicTo 绘制起始终止节点间的曲线连接线
       以此类推
       
3.用法
    长按添加控制点
    长按出弹窗，点击确定删除控制点
    目前最多可添加8个控制点
    头尾控制点不可删除
             


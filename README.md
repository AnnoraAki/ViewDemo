# ViewDemo
自定义View全集？

后期会把原来写过的也丢过来

*`Java`与`Kotlin`的都有，请注意 :>*

- 柱状图

  仿照某项目里面的要求进行的二次更新
  
  主要修复了当时的卡顿问题以及GPU绘制图展现过于夸装的问题

- 简单的可响应右侧`drawable`的`EditText`
  
  自定义`View`讲课用的简单demo
  
  在原有的`ClearEditText`的基础上做了自扩展
  
- 滑动`ViewGroup` **(制作中)**
  
  灵感来源:知乎的滑动卡片
  
  而且之前也没有接触过`ViewGroup`的自定义View绘制，这次就稍微尝试一下
  
  争取做到一定的健壮性吧
  
  一定不借鉴代码！
  
   当前bugList:
    - `onMeasure`的宽高测量有问题
    
      只能暂时用`padding`来进行扩张
      
    - 滑动后卡片位置显示有问题（过渡也有问题）
    
   todoList：
   
   - 数据装填（应该是Adapter形式的接口回调）
  

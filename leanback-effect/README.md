## 1、说明
本库提供TV开发中常用内容：布局、动画、焦点处理等

## 2 `LeanbackEffectLayout`
>该布局为TV端交互控件，即作为与用户操作交互的控件，支持如下功能；
- 阴影
- 边框
- 扫光
- 呼吸灯
- 缩放
- 不同Shape（圆角、圆形、部分圆角等）
- 间距（内容与边框之间的距离）

其他支持：支持ChildView动态添加移除、大小改变、自动修复（确保设置的效果生效）一些极端异常操作带来的问题

具体查看demo和属性定义。

## 3、Leanback Layout
>该系列的布局代码为自动生成，如需生成其他布局，可以参照`bas-leanback-layout-compiler`库配置即可生成；
>该布局为TV端布局控件，主要用于ChildView 边界动画、焦点记忆等。

### 3.1 Leanback Layout
布局为自动生成，目前提供了如下常规布局常：`LeanbackLinearLayout`、`LeanbackFrameLayout`、`LeanbackRelativeLayout`、`LeanbackConstraintLayout`

#### 3.1.1 边界动画支持
在Focused Child View未寻找到下一个焦点时，是否执行边界动画，目前根据KeyEvent采用x轴或y轴抖动动画，后期可以自定义动画；**默认开启**

可以通过xml中配置如下属性禁止边界动画:·`app:borderAnim_bas="false"`
```xml
<bas.leanback.layout.LeanbackLinearLayout 
    app:borderAnim_bas="false">
</bas.leanback.layout.LeanbackLinearLayout>
```
  
#### 3.1.2 ChildView bringToFront优化
修正ChildView在诸如LinearLayout等布局中调用bringToFront方法时引起的问题；对于本身支持的布局则不做额外处理。
该方法没有提供xml属性控制开关，可以通过ViewGroup提供的setChildrenDrawingOrderEnabled(false)强制关闭该优化（不建议关闭，该优化不会带来什么性能消耗问题）。

#### 3.1.3 duplicateChildrenBringToFront_bas
ChildView调用bringToFront方法时，当前布局是否也调用bringToFront，**默认不开启**；
用于解决如下问题：childView调用`bringToFront`方法，只是在当前布局中置于"浮起来"，但是并不意味着在当前布局所在的Parent中浮起来，导致childview还是会被遮住的问题。
```
比如当前布局包含几个childview，每个childView与当前view宽度一样，即布局层次关系可能是这样子：
ViewGroup
    -ViewGroup1（LeanbackFlexboxLayout）
        -ChildView1       
        -ChildView2      
        ...       
    -ViewGroup2  
    ...

ViewGroup2后于ViewGroup1绘制，当ChildView1调用`bringToFront`方法，只是在ViewGroup1中不被其他childView遮住，
但是ViewGroup1还是可能被ViewGroup2遮住；

```

#### 3.1.4 bringChildToFrontWhenRequestFocus_bas
ChildView调用requestFocus时，是否同时调用ChildView的bringToFront方法，**默认不开启**；
该属性作用不大，毕竟ChildView主动调用requestFocus时自己应该清楚自己是否应该调用bringToFont方法；
但该属性在ChildView嵌套的情况下可以避免遮住的问题，类似与3.1.3更多View层级的情况。

#### 3.1.5 focusMemory_bas焦点记忆 + focusMemoryStateType_bas焦点记忆类型
是否启用焦点记忆，如果启用焦点记忆，那么当布局重新获取焦点时，会优先之前获取焦点的View得到焦点，
并根据焦点记忆类型设置获取焦点view的状态为true，其他child view则为false，**默认不开启**；
focusMemoryStateType_bas焦点记忆类型支持如下三种值：
- none：不修改View的状态
- selected：即保持当前焦点记忆的[View]设置[View.setSelected]为true，而其他view则设置为false
- activated：即保持当前焦点记忆的[View]设置[View.setActivated]为true，而其他view则设置为false

使用场景：布局做选中状态、或重新获取焦点时让之前的View获取焦点




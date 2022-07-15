package bas.droid.recyclerview.doc

/**
 * Adapter中方法调用的顺序
 */
/*

1、getItemViewType(获取显示类型，返回值可在onCreateViewHolder中拿到，以决定加载哪种ViewHolder)

2、onCreateViewHolder(加载ViewHolder的布局)

3、onViewAttachedToWindow（当Item进入这个页面的时候调用）

4、onBindViewHolder(将数据绑定到布局上，以及一些逻辑的控制就写这啦)

5、onViewDetachedFromWindow（当Item离开这个页面的时候调用）

6、onViewRecycled(当Item被回收的时候调用)

作者：enchanted1107
链接：https://www.jianshu.com/p/458a4f6a1644
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

【注意】onViewRecycled的调用时机说明：并不是onViewDetachedFromWindow调用之后就一定会立即调用该方法。
RecyclerView在此处使用了内存缓存，通过查看源码，可以发现mViewCacheMax默认为2（使用RecyclerView.setItemViewCacheSize(int size)设置 ），
也就是说当某个holder中的View被DetachFromWindow后，不会立即被回收。如果想要禁用缓存机制，可以使用RecyclerView.setItemViewCacheSize(0)。

http://mesonwang.com/2017/11/25/When-Method-onViewRecycled-Call-in-RecyclerView/

*/

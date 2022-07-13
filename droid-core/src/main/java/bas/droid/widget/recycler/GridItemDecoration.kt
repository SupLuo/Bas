package bas.droid.widget.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * 关于分割线的额外说明
 * 为保证item的间距符合预期，要考虑以下两种场景
 * 1、如果ItemView的Size是用户指定的大小，则不需要注意任何其他情况。
 *
 * 2、如果ItemView的大小是match_parent，或者是类似约束布局中跟据宽度/高度比例计算另一边，则应该保留左右间距，并且左右间距是item之间间距的一半大小。
 * 原因： 网格布局布局中的Item会等分整个布局宽度，假如布局宽度100dp，一行显示4个item，则每个item宽度25dp，并期望item之间的间距8dp。
 * 假如第一列左侧间距10dp，第一个item右侧偏移8dp，那么第一个item的内容宽度为25-10-8=7dp，而第二个item宽度为25-8=17dp，导致两个item内容大小不是期望。
 * 解决办法：因此item之间的间距分配给相邻的item，即左右各自偏移4dp，那么第一个item内容宽度为25-10-4 = 13dp，第二个item内容宽度为25-4-4=17dp，两者还存在内容偏差的原因是因为四周的间距与item间距大小偏差过大
 * 解决办法：调整四周间距大小为item间距的一半，那么第一个item内容宽度为25-4-4 = 17dp，第二个item内容宽度为25-4-4=17dp，两者相等，
 *
 */
class GridItemDecoration private constructor(builder: Builder) :
    androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    private val mDivider: Drawable?
    private val spanCount: Int
    private val itemSpaceSize: Int
    private val includeLREdge: Boolean
    private val includeTBEdge: Boolean
    private val drawLREdge: Boolean
    private val drawTBEdge: Boolean
    private val spanSizeLookup: GridLayoutManager.SpanSizeLookup?
    internal var horizontalSpaceSize: Int = 0
    internal var verticalSpaceSize: Int = 0

    init {
        mDivider = builder.divider
        spanCount = builder.spanCount
        itemSpaceSize = builder.itemSpaceSize
        includeLREdge = builder.includeLREdge
        horizontalSpaceSize = builder.horizontalSpaceSize
        includeTBEdge = builder.includeTBEdge
        verticalSpaceSize = builder.verticalSpaceSize
        drawLREdge = builder.drawLREdge
        drawTBEdge = builder.drawTBEdge
        spanSizeLookup = builder.spanSizeLookup
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view) // item position
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return getItemOffsets(outRect, view, parent, state, layoutManager)
        }

        val column = itemPosition % spanCount // item column
        if (includeLREdge) {
            outRect.left = itemSpaceSize - column * itemSpaceSize / spanCount
            outRect.right = (column + 1) * itemSpaceSize / spanCount
        } else {
            outRect.left = column * itemSpaceSize / spanCount
            outRect.right = itemSpaceSize - (column + 1) * itemSpaceSize / spanCount
        }
        if (includeTBEdge) {
            if (itemPosition < spanCount) outRect.top = itemSpaceSize // top edge
            outRect.bottom = itemSpaceSize // item bottom
        } else {
            if (itemPosition >= spanCount) outRect.top = itemSpaceSize // item top
        }
    }


    private fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State,
        layoutManager: GridLayoutManager
    ) {
//        return outRect.set(10,10,10,10)
        val itemPosition = parent.getChildAdapterPosition(view) // item position
        val spanCount = layoutManager.spanCount
        val spanSizeLookup = layoutManager.spanSizeLookup
        //所在的行/或列
        val spanIndex = spanSizeLookup.getSpanIndex(itemPosition, spanCount)
        val groupIndex = spanSizeLookup.getSpanGroupIndex(itemPosition, spanCount)
        val spanSize = spanSizeLookup.getSpanSize(itemPosition)

        val horizontalOffset: Int = if (includeLREdge) {
            horizontalSpaceSize
        } else {
            0
        }
        val verticalOffset: Int = if (includeTBEdge) {
            verticalSpaceSize
        } else {
            0
        }

        val itemOffset = itemSpaceSize / 2

        //处理规则：四周间隙（上下左右边距）设置在相邻item，item之间的间距由相邻item均分（均分可以让item的偏移更均分
        // 举个例子：假如屏幕宽100dp，两列，则一个item就是50dp，如果item之间的间距放在第一个item，会导致第一个item的content比第二个item的content更窄，
        // 如果item是根据宽计算高（比如约束布局item），则会导致两个item的高度也不一致）
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
            if (spanIndex == 0) {
                //第一列
                outRect.left = horizontalOffset
            } else {
                outRect.left = itemOffset
            }

            if (spanSize < spanCount) {
                var currentPositionSpanIndex = spanIndex
                var currentPosition = itemPosition
                var rowTotalSpanSize = spanSize
                while (currentPositionSpanIndex > 0) {
                    currentPosition--
                    currentPositionSpanIndex =
                        spanSizeLookup.getSpanIndex(currentPosition, spanCount)
                    rowTotalSpanSize += spanSizeLookup.getSpanSize(currentPosition)
                }
                if (rowTotalSpanSize < spanCount) {
                    //不是最后一列,右侧偏移item space size
                    outRect.right = itemOffset
                } else {
                    //最后一列，右侧偏移水平size
                    outRect.right = horizontalOffset
                }
            } else {
                //最后一列，右侧偏移水平size
                outRect.right = horizontalOffset
            }

            if (groupIndex == 0) {
                //第一行，上边偏移垂直size
                outRect.top = verticalOffset
            } else {
                outRect.top = itemOffset
            }

            if (verticalOffset > 0 && spanSizeLookup.getSpanGroupIndex(
                    parent.adapter?.itemCount ?: 0, spanCount
                ) == groupIndex
            ) {
                //最后一行
                outRect.bottom = verticalOffset
            } else {
                outRect.bottom = itemOffset
            }
        } else {
            //水平排布方向
            if (spanIndex == 0) {
                //第一行
                outRect.top = verticalOffset
            } else {
                outRect.top = itemOffset
            }

            if (spanSize < spanCount) {
                var currentPositionSpanIndex = spanIndex
                var currentPosition = itemPosition
                var columnTotalSpanSize = spanSize
                while (currentPositionSpanIndex > 0) {
                    currentPosition--
                    currentPositionSpanIndex =
                        spanSizeLookup.getSpanIndex(currentPosition, spanCount)
                    columnTotalSpanSize += spanSizeLookup.getSpanSize(currentPosition)
                }
                if (columnTotalSpanSize < spanCount) {
                    //不是最后一行,底部偏移item space size
                    outRect.right = itemOffset
                } else {
                    //最后一行，底部偏移垂直size
                    outRect.right = verticalOffset
                }
            } else {
                //最后一行，底部偏移垂直size
                outRect.bottom = verticalOffset
            }

            if (groupIndex == 0) {
                //第一列，左边偏移
                outRect.left = horizontalOffset
            } else {
                outRect.left = itemOffset
            }

            if (verticalOffset > 0 && spanSizeLookup.getSpanGroupIndex(
                    parent.adapter?.itemCount ?: 0, spanCount
                ) == groupIndex
            ) {
                //最后一列
                outRect.right = horizontalOffset
            } else {
                outRect.right = itemOffset
            }
        }
        Log.d(
            "SSSSS",
            "left=${outRect.left} top=${outRect.top} right=${outRect.right} bottom=${outRect.bottom}"
        )
    }

    override fun onDraw(
        c: Canvas,
        parent: androidx.recyclerview.widget.RecyclerView,
        state: androidx.recyclerview.widget.RecyclerView.State
    ) {
//        mDivider?.run {
//            val childCount = parent.childCount
//            for (i in 0 until childCount) {
//                val child = parent.getChildAt(i)
//                //drawHorizontal
//                val remainder = i % spanCount
//                if (i >= spanCount) {
//                    var left = child.left - spaceSize
//                    if (remainder == 0) left = child.left
//                    var right = child.right
////                    //add by lucio:解决最后一个非整除列item 有个小黑边框未封闭的问题
////                    if (i == childCount - 1 && (i + 1) % spanCount != 0) {
////                        right += spaceSize
////                    }
//                    val top = child.top - spaceSize
//                    val bottom = top + spaceSize
//                    mDivider.setBounds(left, top, right, bottom)
//                    mDivider.draw(c)
//                }
//
//                //drawVertical divider
//                if (remainder != 0) {
//                    val top = child.top
//                    val bottom = child.bottom
//                    val left = child.left - spaceSize
//                    val right = left + spaceSize
//                    mDivider.setBounds(left, top, right, bottom)
//                    mDivider.draw(c)
//                }
//            }
//
////            drawHorizontal(c, parent)
////            drawVertical(c, parent)
//            if (includeLREdge && drawLREdge) drawLR(c, parent)
//            if (includeTBEdge && drawTBEdge) drawTB(c, parent)
//        }
    }

    private fun drawLR(c: Canvas, parent: androidx.recyclerview.widget.RecyclerView) {
        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            //最左边那条线
            if (i % spanCount == 0) {
                val left = child.left - itemSpaceSize
                val right = left + itemSpaceSize
                val bottom = child.bottom
                var top = child.top - itemSpaceSize
                if (i == 0) top = child.top //【左上方】那一块交给drawTB绘制
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
            //最右边那条线
            if ((i + 1) % spanCount == 0) {
                val left = child.right
                val right = left + itemSpaceSize
                val bottom = child.bottom
                var top = child.top - itemSpaceSize
                if (i == spanCount - 1) top = child.top //【右上方】那一块交给drawTB绘制
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    private fun drawTB(c: Canvas, parent: androidx.recyclerview.widget.RecyclerView) {
        val childCount = parent.childCount
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            //最上边那条线
            if (i < spanCount) {
                val top = child.top - itemSpaceSize
                val bottom = top + itemSpaceSize
                val left = child.left
                var right = child.right + itemSpaceSize
                if ((i + 1) % spanCount == 0 || childCount < spanCount && i == childCount - 1)
                    right = child.right  //上边最右边那条线已经绘制了
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
            //最下边那条线
            if (childCount % spanCount == 0 && i >= spanCount * (childCount / spanCount - 1)) {
                val top = child.bottom
                val bottom = top + itemSpaceSize
                val left = child.left - itemSpaceSize
                var right = child.right
                if ((i + 1) % spanCount == 0) right = child.right + itemSpaceSize    //最右边那条线
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            } else if (i >= spanCount * (childCount / spanCount)) {
                val top = child.bottom
                val bottom = top + itemSpaceSize
                val right = child.right
                var left = child.left - itemSpaceSize
                if (!drawLREdge && i % spanCount == 0) left = child.left //最左边那条线
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    /**
     * 网格分割线构造器
     */
    class Builder {
        internal var divider: Drawable? = null
        internal var spanCount: Int = 1
        internal var itemSpaceSize: Int = 0

        /**
         * 水平方向间距大小（即左右两边的宽度）
         */
        internal var horizontalSpaceSize: Int = 0
        internal var includeLREdge = false

        internal var verticalSpaceSize: Int = 0
        internal var includeTBEdge = false
        internal var drawLREdge = true
        internal var drawTBEdge = true

        internal var spanSizeLookup: GridLayoutManager.SpanSizeLookup? = null

        /**
         * 设定分割线图片
         */
        fun setDivider(drawable: Drawable): Builder {
            divider = drawable
            return this
        }

        /**
         * 设置分割线颜色 与＃setDivider方法互斥
         */
        fun setDividerColor(color: Int): Builder {
            divider = ColorDrawable(color)
            return this
        }

        /**
         * 行数或列数
         */
        fun setSpanCount(count: Int): Builder {
            spanCount = count
            return this
        }

        /**
         * 行列间距大小
         */
        fun setItemSpaceSize(size: Int): Builder {
            itemSpaceSize = size
            return this
        }

        /**
         * 是否包含左右边界
         */
        fun setIncludeLREdge(include: Boolean, spaceSize: Int): Builder {
            includeLREdge = include
            horizontalSpaceSize = spaceSize
            return this
        }

        /**
         * 是否包含上下边界
         */
        fun setIncludeTBEdge(include: Boolean, spaceSize: Int): Builder {
            includeTBEdge = include
            verticalSpaceSize = spaceSize
            return this
        }

        /**
         * 是否绘制左右边界（默认绘制，如果为false，则不用分割线颜色绘制左右边界）
         */
        fun setDrawLREdge(draw: Boolean): Builder {
            drawLREdge = draw
            return this
        }

        /**
         * 是否绘制上下边界（默认绘制，如果为false，则不用分割线颜色绘制左右边界）
         */
        fun setDrawTBEdge(draw: Boolean): Builder {
            drawTBEdge = draw
            return this
        }

        fun setSpanSizeLookup(spanSizeLookup: GridLayoutManager.SpanSizeLookup): Builder {
            this.spanSizeLookup = spanSizeLookup
            return this
        }

        fun build(): GridItemDecoration {
            return GridItemDecoration(this)
        }
    }
}
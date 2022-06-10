/**
 * Created by Lucio on 2021/9/27.
 */

@file:JvmName("Views")

package bas.droid.core.view.extensions

import android.content.Context
import android.view.View
import android.view.ViewGroup
import bas.droid.core.ui.tryUi
import bas.lib.core.lang.orDefault

typealias ViewClickLambda = ((View) -> Unit)?

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }


inline var View.isVisibleOrNot: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

/**
 * 从ViewGroup移除自身
 */
inline fun View?.removeSelfFromParent() {
    if (this == null)
        return
    (this.parent as? ViewGroup)?.removeView(this)
}

/**
 * 能否获取焦点
 */
val View.canTakeFocus: Boolean
    get() = isFocusable && isVisible && isEnabled


inline fun View.updateLayoutParamsOrDefault(block: ViewGroup.LayoutParams.() -> Unit) {
    var lp = layoutParams.orDefault {
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    block(lp)
    layoutParams = lp
}

inline fun View.runContextCatchingBlock(block: Context.() -> Unit) {
    context.run {
        tryUi {
            block(this)
        }
    }
}

package andme.core.widget

import android.view.View


inline fun View.setVisible() {
    visibility = View.VISIBLE
}

inline fun View.setInVisible() {
    visibility = View.INVISIBLE
}

@Deprecated("使用gone", replaceWith = ReplaceWith("gone()"))
inline fun View.setGone() {
    visibility = View.GONE
}

inline fun View.gone() {
    if (visibility != View.GONE)
        visibility = View.GONE
}
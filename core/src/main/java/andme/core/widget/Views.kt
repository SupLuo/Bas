package andme.core.widget

import android.view.View




inline fun View.setVisible() {
    visibility = View.VISIBLE
}

inline fun View.setInVisible() {
    visibility = View.INVISIBLE
}

inline fun View.setGone() {
    visibility = View.GONE
}
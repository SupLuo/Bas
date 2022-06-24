@file:JvmName("ToastsKt")
package bas.droid.core.ui

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Toast帮助类:可以通过修改该类改变行为
 */
var toastUi: ToastUI = SystemToastUI


fun Context.toast(msg: String) {
    toastUi.showToast(this, msg)
}

fun Context.toast(msg: String, length: Int) {
    toastUi.showToast(this, msg, length)
}

fun Fragment.toast(msg: String) {
    activity?.let {
        toastUi.showToast(it, msg)
    }
}

fun Fragment.toast(msg: String, length: Int) {
    activity?.let {
        toastUi.showToast(it, msg, length)
    }
}

fun View.toast(msg: String) {
    toastUi.showToast(context, msg)
}

fun View.toast(msg: String, length: Int) {
    toastUi.showToast(context, msg, length)
}


interface ToastUI {

    fun showToast(ctx: Context, msg: String)

    fun showToast(ctx: Context, msg: String, length: Int)

}

/**
 * 默认对话框实现
 */
object SystemToastUI : ToastUI {

    override fun showToast(ctx: Context, msg: String) {
        showToast(ctx, msg, Toast.LENGTH_SHORT)
    }

    override fun showToast(ctx: Context, msg: String, length: Int) {
        Toast.makeText(ctx, msg, length).show()
    }
}
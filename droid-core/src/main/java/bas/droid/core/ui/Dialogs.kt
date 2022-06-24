/**
 * Created by Lucio on 2020-11-02.
 * 对话框帮助类
 */
@file:JvmName("DialogsKt")

package bas.droid.core.ui

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * 对话框帮助类:可以通过修改该类改变行为
 */
var dialogUi: DialogUI = SystemDialogUI

fun Context.showLoading(message: CharSequence): ProgressDialog {
    return dialogUi.showLoading(this, message)
}

fun Context.showAlertDialog(@StringRes messageId: Int, @StringRes positiveTextId: Int): Dialog {
    return dialogUi.showAlertDialog(this, messageId, positiveTextId)
}

fun Context.showAlertDialog(message: CharSequence, positiveBtnText: CharSequence): Dialog {
    return dialogUi.showAlertDialog(this, message, positiveBtnText)
}

fun Context.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return dialogUi.showAlertDialog(this, message, okPair, cancelable)
}

fun Context.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return dialogUi.showAlertDialog(this, messageId, okPair, cancelable)
}

fun Context.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelPair: Pair<CharSequence, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return dialogUi.showAlertDialog(this, message, okPair, cancelPair, cancelable)
}

fun Context.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelPair: Pair<Int, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return dialogUi.showAlertDialog(this, messageId, okPair, cancelPair, cancelable)
}

fun Fragment.showLoading(message: CharSequence): ProgressDialog {
    return requireContext().showLoading(message)
}

fun Fragment.showAlertDialog(@StringRes messageId: Int, @StringRes positiveTextId: Int): Dialog {
    return requireContext().showAlertDialog(messageId, positiveTextId)
}

fun Fragment.showAlertDialog(message: CharSequence, positiveBtnText: CharSequence): Dialog {
    return requireContext().showAlertDialog(message, positiveBtnText)
}

fun Fragment.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return requireContext().showAlertDialog(message, okPair, cancelable)
}

fun Fragment.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return requireContext().showAlertDialog(messageId, okPair, cancelable)
}

fun Fragment.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelPair: Pair<CharSequence, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return requireContext().showAlertDialog(message, okPair, cancelPair, cancelable)
}

fun Fragment.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelPair: Pair<Int, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return requireContext().showAlertDialog(messageId, okPair, cancelPair, cancelable)
}


fun View.showLoading(message: CharSequence): ProgressDialog {
    return context.showLoading(message)
}

fun View.showAlertDialog(@StringRes messageId: Int, @StringRes positiveTextId: Int): Dialog {
    return context.showAlertDialog(messageId, positiveTextId)
}

fun View.showAlertDialog(message: CharSequence, positiveBtnText: CharSequence): Dialog {
    return context.showAlertDialog(message, positiveBtnText)
}

fun View.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return context.showAlertDialog(message, okPair, cancelable)
}

fun View.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelable: Boolean = true
): Dialog {
    return context.showAlertDialog(messageId, okPair, cancelable)
}

fun View.showAlertDialog(
    message: CharSequence,
    okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
    cancelPair: Pair<CharSequence, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return context.showAlertDialog(message, okPair, cancelPair, cancelable)
}

fun View.showAlertDialog(
    @StringRes messageId: Int,
    okPair: Pair<Int, DialogInterface.OnClickListener?>,
    cancelPair: Pair<Int, DialogInterface.OnClickListener?>? = null,
    cancelable: Boolean = true
): Dialog {
    return context.showAlertDialog(messageId, okPair, cancelPair, cancelable)
}

interface DialogUI {

    fun showLoading(ctx: Context, message: CharSequence): ProgressDialog

    fun showAlertDialog(
        ctx: Context,
        @StringRes messageId: Int,
        @StringRes positiveTextId: Int
    ): Dialog {
        return showAlertDialog(ctx, ctx.getText(messageId), ctx.getText(positiveTextId))
    }

    fun showAlertDialog(ctx: Context, message: CharSequence, positiveBtnText: CharSequence): Dialog

    fun showAlertDialog(
        ctx: Context,
        message: CharSequence,
        okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
        cancelable: Boolean
    ): Dialog {
        return showAlertDialog(ctx, message, okPair, null, cancelable)
    }

    fun showAlertDialog(
        ctx: Context,
        @StringRes messageId: Int,
        okPair: Pair<Int, DialogInterface.OnClickListener?>,
        cancelable: Boolean
    ): Dialog {
        return showAlertDialog(ctx, messageId, okPair, null, cancelable)
    }

    fun showAlertDialog(
        ctx: Context, message: CharSequence,
        okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
        cancelPair: Pair<CharSequence, DialogInterface.OnClickListener?>? = null,
        cancelable: Boolean = true
    ): Dialog

    fun showAlertDialog(
        ctx: Context, @StringRes messageId: Int,
        okPair: Pair<Int, DialogInterface.OnClickListener?>,
        cancelPair: Pair<Int, DialogInterface.OnClickListener?>? = null,
        cancelable: Boolean = true
    ): Dialog

}

interface Dialog {

    fun dismiss()

    val isShowing: Boolean
}

interface ProgressDialog : Dialog {

    fun setMessage(msg: String)

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?)
}

package bas.droid.core.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.Fragment
import bas.lib.core.exception.CommonExceptionHandler
import bas.lib.core.exception.ExceptionHandler
import bas.lib.core.exception.friendlyMessage
import bas.lib.core.exceptionHandler
import bas.lib.core.runOnDebug

interface DroidExceptionHandler : ExceptionHandler {
    fun handleUIException(context: Context, e: Throwable)
}

open class DefaultDroidExceptionHandler : CommonExceptionHandler(), DroidExceptionHandler {
    override fun handleCatchException(e: Throwable) {
        runOnDebug {
            e.printStackTrace()
        }
    }

    override fun handleUIException(context: Context, e: Throwable) {
        dialogUi.showAlertDialog(context, e.friendlyMessage, "确定")
    }
}

/**
 * 初始化Droid异常帮助类：如果libcore的可用则直接使用droid，否则使用默认的handler
 */
internal fun initDroidExceptionHandler(): DroidExceptionHandler {
    return exceptionHandler as? DroidExceptionHandler ?: DefaultDroidExceptionHandler()
}

var droidExceptionHandler: DroidExceptionHandler = initDroidExceptionHandler()
    set(value) {
        exceptionHandler = value
        field = value
    }

/**
 * 捕获ui异常
 */
inline fun Context.tryOnCreate(action: () -> Unit): Throwable? {
    return try {
        action()
        null
    } catch (e: Throwable) {
        droidExceptionHandler.handleCatchException(e)
        showAlertDialog(
            "界面初始化失败:${e.friendlyMessage}",
            Pair("退出", DialogInterface.OnClickListener { dialog, which ->
                (this as? Activity)?.finish()
            }),
            false
        )
        e
    }
}


/**
 * 捕获ui异常
 */
inline fun Context.tryUi(action: () -> Unit): Throwable? {
    return try {
        action()
        null
    } catch (e: Throwable) {
        droidExceptionHandler.handleUIException(this, e)
        e
    }
}

inline fun View.tryUi(action: () -> Unit): Throwable? {
    return context.tryUi(action)
}

inline fun Fragment.tryUi(action: () -> Unit): Throwable? {
    return try {
        action()
        null
    } catch (e: Throwable) {
        val ac = activity
        if (ac == null || !this.isAdded) {
            e.printStackTrace()
            return e
        } else {
            droidExceptionHandler.handleUIException(ac, e)
        }
        e
    }
}



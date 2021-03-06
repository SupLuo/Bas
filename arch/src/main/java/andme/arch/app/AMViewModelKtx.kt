package andme.arch.app

import bas.lib.core.coroutines.launchRetryable
import bas.droid.core.ui.showAlertDialog
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bas.droid.core.ui.droidExceptionHandler
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by Lucio on 2020-11-18.
 */

/**
 * launch方法的增强版，支持重试，类似RxJava的retryWhen效果
 * 当捕获了[block]执行过程中抛出的[RetryException]时，将会重新执行[block]
 */
fun ViewModel.launchRetryable(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launchRetryable(context, start, block)
}

fun ViewModel.launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
):Job {
    return viewModelScope.launch(context, start, block)
}


inline fun AMViewModel.tryUi(func: AMViewModel.() -> Unit): Throwable? {
    return try {
        func(this)
        null
    } catch (e: Throwable) {
        invokeContextAction {
            droidExceptionHandler.handleUIException(it, e)
        }
        e
    }
}


fun AMViewModel.showAlertDialog(@StringRes messageId: Int, @StringRes positiveTextId: Int) {
    invokeContextAction {
        it.showAlertDialog(messageId, positiveTextId)
    }
}

fun AMViewModel.showAlertDialog(message: CharSequence, positiveBtnText: CharSequence) {
    invokeContextAction {
        it.showAlertDialog(message, positiveBtnText)
    }
}

fun AMViewModel.showAlertDialog(message: CharSequence, okPair: Pair<CharSequence, DialogInterface.OnClickListener?>, cancelable: Boolean = true) {
    invokeContextAction {
        it.showAlertDialog(message, okPair, cancelable)
    }
}

fun AMViewModel.showAlertDialog(@StringRes messageId: Int, okPair: Pair<Int, DialogInterface.OnClickListener?>, cancelable: Boolean = true) {
    invokeContextAction {
        it.showAlertDialog(messageId, okPair, cancelable)
    }
}

fun AMViewModel.showAlertDialog(
        message: CharSequence,
        okPair: Pair<CharSequence, DialogInterface.OnClickListener?>,
        cancelPair: Pair<CharSequence, DialogInterface.OnClickListener?>? = null,
        cancelable: Boolean = true
) {
    invokeContextAction {
        it.showAlertDialog(message, okPair, cancelPair, cancelable)
    }
}

fun AMViewModel.showAlertDialog(
        @StringRes messageId: Int,
        okPair: Pair<Int, DialogInterface.OnClickListener?>,
        cancelPair: Pair<Int, DialogInterface.OnClickListener?>? = null,
        cancelable: Boolean = true
) {
    invokeContextAction {
        it.showAlertDialog(messageId, okPair, cancelPair, cancelable)
    }
}
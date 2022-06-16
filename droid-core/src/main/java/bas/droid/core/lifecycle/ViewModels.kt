package bas.droid.core.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import bas.lib.core.coroutines.launchRetryable
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SingleViewModelFactory(val creator: () -> ViewModel) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator.invoke() as T
    }
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return viewModelScope.launch(context, start, block)
}

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


fun ViewModel.launchWithDelay(
    delay: Long,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context, start) {
        delay(delay)
        block.invoke(this)
    }
}
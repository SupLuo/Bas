/**
 * 防抖动事件
 */
@file:JvmName("DebounceKt")

package bas.droid.core.view.extensions

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import bas.droid.core.R
import bas.lib.core.coroutines.throttleFirst
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val DEFAULT_CLICK_THRESHOLD = 300L

/**
 * 防抖动点击流 ，效果等同于[set]
 */
fun View.debounceClickFlow(
    threshold: Long = DEFAULT_CLICK_THRESHOLD,
    click: suspend (View) -> Unit
) = callbackFlow {
    setOnClickListener {
        println("view response click")
        val result = trySend(Unit)
        if (result.isSuccess) {
            return@setOnClickListener
        }
        result.exceptionOrNull()?.printStackTrace()
    }
    awaitClose {
        setOnClickListener(null)
    }
}.throttleFirst(threshold).onEach {
    click.invoke(this)
}

fun View.debounceClick(
    lifecycleOwner: LifecycleOwner,
    threshold: Long = DEFAULT_CLICK_THRESHOLD,
    click: suspend (View) -> Unit
) {
//    lifecycleOwner.lifecycleScope.launch {
//        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
//            debounceClickFlow(threshold, click).collect()
//        }
//    }
    debounceClickFlow(threshold, click).launchIn(lifecycleOwner.lifecycleScope)
}

/**
 * 没有使用[JvmOverloads]方式提供扩展，不方便调用
 */
fun View.setOnDebounceClickListener(click: View.OnClickListener?) {
    setOnDebounceClickListener(DEFAULT_CLICK_THRESHOLD, click)
}

/**
 * 防抖动点击，效果等同于[debounceClickFlow]
 */
fun View.setOnDebounceClickListener(
    threshold: Long,
    click: View.OnClickListener?
) {
    if (click == null) {
        setOnClickListener(null)
        return
    }
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - this.clickTriggerTime >= threshold) {
            // 如果不是快速点击，则响应点击逻辑
            this.clickTriggerTime = currentTime
            click.onClick(it)
        }
    }
}

// 记录上次点击时间
private var View.clickTriggerTime: Long
    get() = getTag(R.id.click_trigger_time) as? Long ?: 0L
    set(value) = setTag(R.id.click_trigger_time, value)

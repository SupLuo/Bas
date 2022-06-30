package bas.droid.arch

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import bas.droid.core.ui.ProgressDialog
import bas.droid.core.ui.dialogUi
import bas.droid.core.ui.droidExceptionHandler
import bas.droid.core.ui.toastUi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 用于处理ViewModel中的实践
 * 参考：https://developer.android.google.cn/jetpack/guide/ui-layer/events#handle-viewmodel-events
 */

sealed class ViewModelEventMessage(val id: Long, val extra: Any?) {

    protected var target: ViewModelArch? = null

    internal fun setViewModelTargetIfNull(target: ViewModelArch?) {
        this.target = target
    }

    /**
     * 处理消息
     */
    internal fun resolve(ui: UserUi) {
        try {
            handleInternal(ui)
        } finally {
            onHandled()
        }
    }

    /**
     * 处理本条消息
     */
    protected abstract fun handleInternal(ui: UserUi)

    protected open fun onHandled() {
        this.target?.onEventMessageHandled(this)
        this.target = null
    }

    /**
     * 显示toast
     */
    class ToastMessage @JvmOverloads constructor(
        id: Long = System.nanoTime(),
        val message: String,
        val duration: Int = Toast.LENGTH_SHORT,
        extra: Any? = null
    ) : ViewModelEventMessage(id, extra) {

        override fun handleInternal(ui: UserUi) {
            toastUi.showToast(ui.realCtx, message, duration)
        }
    }

    /**
     * 显示loading alert
     *
     * todo: 目前一条loading 对应一个hide loading，如需使用多个，请通过不同的id实现
     */
    class LoadingAlertMessage @JvmOverloads constructor(
        val message: String,
        id: Long = System.nanoTime(),
        extra: Any? = null
    ) :
        ViewModelEventMessage(id, extra) {

        override fun handleInternal(ui: UserUi) {
            //显示当前id之前，先隐藏之前显示的相同id的对话框
            ui.hideLoadingProgressInternal(id.toString())
            val dialog = dialogUi.showLoading(ui.realCtx, message)
            ui.setTagIfAbsentBas(id.toString(), dialog)
        }
    }

    /**
     * 隐藏loading alert
     * todo: 目前一条hide loading 对应一个 loading，如需使用多个，请通过不同的id实现
     * @param id 本消息id
     * @param showMessageId 需要关闭的消息id
     */
    class HideLoadingAlertMessage @JvmOverloads constructor(
        val showMessageId: Long,
        id: Long = System.nanoTime(),
        extra: Any? = null
    ) : ViewModelEventMessage(id, extra) {

        fun dismiss() {
            target?.sendHideLoadingAlertMessage(this)
        }

        override fun handleInternal(ui: UserUi) {
            ui.hideLoadingProgressInternal(showMessageId.toString())
        }
    }

    class UiExceptionMessage @JvmOverloads constructor(
        val error: Throwable,
        id: Long = System.nanoTime(),
        extra: Any? = null
    ) :
        ViewModelEventMessage(id, extra) {

        override fun handleInternal(ui: UserUi) {
            droidExceptionHandler.handleUIException(ui.realCtx, error)
        }
    }
}

fun UserUi.registerViewModeEventHandler(viewModel: ViewModelArch, state: Lifecycle.State) {
    val ui = this@registerViewModeEventHandler
    lifecycleScope.launch {
        val viewLifecycleOwner: LifecycleOwner =
            (ui as? Fragment)?.viewLifecycleOwner ?: ui
        viewLifecycleOwner.repeatOnLifecycle(state) {
            viewModel.eventUiState.collectLatest {
                it.firstOrNull()?.resolve(ui)
            }
        }
    }
}

internal fun UserUi.hideLoadingProgressInternal(key: String) {
    val previous = this.getTagBasAndRemove<ProgressDialog>(key)
    previous?.dismiss()
}
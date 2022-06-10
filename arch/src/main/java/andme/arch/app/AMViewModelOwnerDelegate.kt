package andme.arch.app

import bas.lib.core.lang.annotation.Note
import bas.lib.core.lang.applyWhen
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import bas.droid.core.ui.toast
import java.lang.reflect.ParameterizedType

/**
 * [AMViewModel]的事件绑定、实现等代理类
 */
open class AMViewModelOwnerDelegate<VM : ViewModel> constructor(open val realOwner: AMViewModelOwner) : AMViewModelOwner by realOwner {

    lateinit var viewModel: VM
        private set

    open fun onCreate(savedInstanceState: Bundle?, vmClass: Class<VM>) {
        viewModel = getViewModelProvider().get(vmClass)
        initViewModelEvent(viewModel, false)
    }

    /**
     * @param autoBindOwnerIfMatch 如果获取的ViewModel是[AMViewModel]是否自动绑定事件，默认自动绑定
     */
    fun <T : ViewModel> obtainViewModel(clazz: Class<T>, autoBindOwnerIfMatch: Boolean = true): T {
        return getViewModelProvider().get(clazz).also {
            vm->
            if (autoBindOwnerIfMatch && vm is AMViewModel && !vm.hasBindOwner) {
                initViewModelEvent(vm, true)
            }
        }
    }

    /**
     * 初始化viewmodel相关事件
     * @param removePrevious 是否在绑定事件之前先调用移除方法，避免多次绑定
     */
    protected open fun initViewModelEvent(viewModel: ViewModel, removePrevious: Boolean = true) {

        if (viewModel !is AMViewModel)
            return
        //先移除观察，避免重复绑定
        this.applyWhen(removePrevious) {
            unregisterViewModelEvent(viewModel)
        }
        //添加观察
        registerViewModelEvent(viewModel)
    }

    //绑定viewmodel事件
    open fun registerViewModelEvent(viewModel: AMViewModel) {
        viewModel.hasBindOwner = true
        lifecycle.addObserver(viewModel)
        val lifecycleOwner = this
        viewModel.apply {
            finishEvent.observe(lifecycleOwner, Observer {
                onFinishByViewModel()
            })

            backPressedEvent.observe(lifecycleOwner, Observer {
                onBackPressedByViewModel()
            })
            startActivityEvent.observe(lifecycleOwner, Observer<Intent> {
                onStartActivityByViewModel(it)
            })

            startActivityForResultEvent.observe(lifecycleOwner, Observer<Pair<Intent, Int>> {
                startActivityForResultByViewModel(it.first, it.second)
            })

            toastEvent.observe(lifecycleOwner, Observer {
                onToastByViewModel(it.first, it.second)
            })

            contextActionEvent.observe(lifecycleOwner, Observer<AMViewModel.ContextAction> {
                onContextActionByViewModel(it)
            })
        }
    }

    //注销ViewModel事件
    open fun unregisterViewModelEvent(viewModel: AMViewModel) {
        viewModel.unregister(this)
    }

    protected open fun onFinishByViewModel() {
        finish()
    }

    protected open fun onBackPressedByViewModel() {
        onBackPressed()
    }

    protected open fun onStartActivityByViewModel(intent: Intent) {
        startActivity(intent)
    }

    protected open fun startActivityForResultByViewModel(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    protected open fun onToastByViewModel(msg: String, length: Int) {
        realCtx.toast(msg, length)
    }

    protected open fun onContextActionByViewModel(event: AMViewModel.ContextAction) {
        event.onContextAction(realCtx)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (viewModel as? AMViewModel)?.onActivityResult(requestCode, resultCode, data)
    }

}
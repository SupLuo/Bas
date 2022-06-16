package bas.droid.core.app

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import bas.droid.core.content.checkValidationOrThrow
import bas.lib.core.exception.onCatch
import bas.lib.core.exception.tryIgnore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun Fragment.launchBlockRepeatOnLifecycle(
    state: Lifecycle.State,
    noinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state, block)
    }
}

/**
 * 快速运行一个Activity
 */
fun Fragment.startActivity(clazz: Class<out Activity>) {
    val it = Intent(this.requireContext(), clazz)
    startActivity(it)
}

fun Fragment.startActivitySafely(intent: Intent) {
    tryIgnore {
        intent.checkValidationOrThrow(this.requireContext())
        this.startActivity(intent)
    }.onCatch {
        Log.w(this::class.java.simpleName, "无法打开指定Intent", it)
    }
}

fun Fragment.startActivityForResultSafely(intent: Intent, requestCode: Int) {
    tryIgnore {
        intent.checkValidationOrThrow(this.requireContext())
        this.startActivityForResult(intent, requestCode)
    }.onCatch {
        Log.w(this::class.java.simpleName, "无法打开指定Intent", it)
    }
}

/**
 * @see viewModels
 */
@MainThread
fun <VM : ViewModel> Fragment.obtainViewModel(
    clazz: Class<VM>,
    owner: ViewModelStoreOwner = this,
    factory: ViewModelProvider.Factory? = null,
): VM {
    val factoryPromise =
        factory ?: ((owner as? HasDefaultViewModelProviderFactory)?.defaultViewModelProviderFactory
            ?: defaultViewModelProviderFactory)
    val storePromise = owner.viewModelStore
    return ViewModelProvider(storePromise, factoryPromise)[clazz]
}

/**
 * 获取ActivityViewModel
 * @see activityViewModels
 */
@MainThread
fun <VM : ViewModel> Fragment.obtainActivityViewModel(
    clazz: Class<VM>,
    factory: ViewModelProvider.Factory? = null
): VM {
    val factoryPromise = factory ?: defaultViewModelProviderFactory
    val store = requireActivity().viewModelStore
    return ViewModelProvider(store, factoryPromise)[clazz]
}

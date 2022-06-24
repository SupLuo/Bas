package bas.droid.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bas.droid.core.view.extensions.removeFromParent

abstract class FragmentArch : Fragment,UserUi {

//    companion object {
//        private const val EXTRA_VIEW_FLAG = "_view_cacheable_"
//        private const val EXTRA_LAYOUT_ID_FLAG = "_layout_id_"
//    }

    // Can't use ConcurrentHashMap, because it can lose values on old apis (see b/37042460)
    private val bagOfTags: MutableMap<String, Any> = HashMap()

    override val realCtx: Context
        get() = requireContext()

    /**
     * 是否启用View缓存：默认开启,可以设置为false避免view 缓存
     */
    protected open val viewCacheable: Boolean = true

    protected var contentView: View? = null
        private set

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    protected abstract fun createContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (viewCacheable) {
            contentView?.also {
                it.removeFromParent()
            } ?: createContentViewInternal(inflater, container, savedInstanceState).also {
                contentView = it
            }
        } else {
            createContentViewInternal(inflater, container, savedInstanceState)
        }
    }

    private fun createContentViewInternal(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState) ?: createContentView(
            inflater,
            container,
            savedInstanceState
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (viewCacheable) {
            contentView?.removeFromParent()
        } else {
            contentView = null
        }
    }

    override fun <T : Any> setTagIfAbsentBas(key: String, newValue: T): T {
        var previous: T?
        synchronized(bagOfTags) {
            previous = bagOfTags[key] as T?
            if (previous == null) {
                bagOfTags[key] = newValue
            }
        }
        val result = if (previous == null) newValue else previous!!
        return result
    }

    override fun <T : Any> getTagBas(key: String): T? {
        synchronized(bagOfTags) { return bagOfTags[key] as T? }
    }

    override fun <T : Any> getTagBasAndRemove(key: String): T? {
        synchronized(bagOfTags) {
            val value = bagOfTags[key] as T?
            bagOfTags.remove(key)
            return value
        }
    }

    override fun removeTagBas(key: String) {
        synchronized(bagOfTags) {
            bagOfTags.remove(key)
        }
    }
}

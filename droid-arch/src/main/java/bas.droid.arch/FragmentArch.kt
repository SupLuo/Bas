package bas.droid.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bas.droid.core.view.extensions.removeFromParent

abstract class FragmentArch : Fragment {

//    companion object {
//        private const val EXTRA_VIEW_FLAG = "_view_cacheable_"
//        private const val EXTRA_LAYOUT_ID_FLAG = "_layout_id_"
//    }

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
}

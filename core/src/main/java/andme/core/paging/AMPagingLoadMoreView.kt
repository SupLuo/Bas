package andme.core.paging

import andme.core.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter

/**
 * Created by Lucio on 2021/3/21.
 */
class AMPagingLoadMoreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val loadingLayout: View
    val notLoadingLayout: View
    val failLayout: View
    val nomoreLayout: View

    val nomoreText: TextView
    val failText: TextView
    val notloadingText: TextView


    init {
        inflate(context, R.layout.am_paging_loadmore_view, this)
        loadingLayout = findViewById<View>(R.id.am_paging_loading_layout)
        notLoadingLayout = findViewById<View>(R.id.am_paging_complete_layout)
        failLayout = findViewById<View>(R.id.am_paging_fail_layout)
        nomoreLayout = findViewById<View>(R.id.am_paging_no_more_layout)

        nomoreText = findViewById<TextView>(R.id.am_paging_no_more_text)
        failText = findViewById<TextView>(R.id.am_paging_fail_text)
        notloadingText = findViewById<TextView>(R.id.am_paging_complete_text)
    }

    /**
     */
    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(loadState) {
            if (field != loadState) {
                val oldItem = shouldDisplay(field)
                val newItem = shouldDisplay(loadState)

                if (oldItem && !newItem) {
                    visibility = View.GONE
                } else if (newItem && !oldItem) {
                    visibility = View.VISIBLE
                } else if (oldItem && newItem) {
                    applyState(loadState)
                }
                field = loadState
            }
        }

    private fun applyState(loadState: LoadState) {
        loadingLayout.isVisible = loadState is LoadState.Loading
        notLoadingLayout.isVisible =
            loadState is LoadState.NotLoading && !loadState.endOfPaginationReached
        nomoreLayout.isVisible =
            loadState is LoadState.NotLoading && loadState.endOfPaginationReached
        failLayout.isVisible = loadState is LoadState.Error
    }

    private val mLoadStateListener: (CombinedLoadStates) -> Unit = { state ->
        this.loadState = state.append
    }

    /**
     * ??????????????????????????????????????????
     * ??????[PagingDataAdapter.addLoadStateListener]????????????????????????????????????[bindAdapter]?????????????????????
     */
    fun getLoadStateListener(): (CombinedLoadStates) -> Unit {
        return mLoadStateListener
    }

    /**
     * ??????Adapter,
     * ???????????????????????????View??????????????????[PagingDataAdapter]???Footer?????????
     * ????????????[PagingDataAdapter]???FooterAdapter????????????[AMPagingLoadFooterAdapter]??????
     */
    fun bindAdapter(adapter: PagingDataAdapter<*, *>) {
        adapter.removeLoadStateListener(mLoadStateListener)
        adapter.addLoadStateListener(mLoadStateListener)
    }

//    /**
//     * ??????Adapter,
//     * ???????????????????????????View??????????????????[PagingDataAdapter]???Footer??????
//     * ??????????????????rv????????????????????????Paging????????????????????????????????????View??????????????????Paging???????????????????????????????????????view??????
//     */
//    fun bindAdapter(adapter: PagingDataAdapter<*, *>, rv: RecyclerView) {
//        bindAdapter(adapter)
//        rv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//
//        })
//    }

    /**
     * ????????????
     */
    protected fun shouldDisplay(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error || (loadState.endOfPaginationReached && loadState is LoadState.NotLoading)
    }

    private inline var View.isVisible: Boolean
        get() = visibility == View.VISIBLE
        set(value) {
            visibility = if (value) View.VISIBLE else View.GONE
        }
}
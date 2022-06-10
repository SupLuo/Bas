package bas.leanback.support.presenter

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import bas.leanback.core.R

/**
 * Created by Lucio on 2021/3/10.
 */
abstract class BasePresenter<D, V : BaseViewHolder> : Presenter() {

    private var itemClickListener: OnItemClickListener<D, V>? = null
    private var itemFocusChangedListener: OnItemFocusChangedListener<D, V>? = null
    private var itemKeyListener: OnItemKeyListener<D, V>? = null

    private val itemClickListenerProvider: ItemClickProvider<D, V> =
        object : ItemClickProvider<D, V> {
            override fun getItemClickListener(): OnItemClickListener<D, V>? {
                return itemClickListener
            }
        }

    private val itemFocusChangedListenerProvider: ItemFocusChangedListenerProvider<D, V> =
        object : ItemFocusChangedListenerProvider<D, V> {
            override fun getItemFocusChangedListener(): OnItemFocusChangedListener<D, V>? {
                return itemFocusChangedListener
            }
        }

    private val itemKeyListenerProvider: ItemKeyListenerProvider<D, V> = object :
        ItemKeyListenerProvider<D, V> {
        override fun getItemKeyListener(): OnItemKeyListener<D, V>? {
            return itemKeyListener
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener<D, V>?) {
        itemClickListener = listener
    }

    fun setOnItemFocusChangedListener(listener: OnItemFocusChangedListener<D, V>?) {
        itemFocusChangedListener = listener
    }

    fun setOnItemKeyListener(listener: OnItemKeyListener<D, V>?) {
        itemKeyListener = listener
    }

    fun getOnItemFocusChangedListener(): OnItemFocusChangedListener<D, V>? {
        return itemFocusChangedListener
    }

    protected abstract fun bindViewHolder(viewHolder: V, item: D)

    /**
     * Binds a [View] to an item.
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any?) {
        val data = item as D
        val holder = viewHolder as V
        if (itemClickListener != null) {
            var clickHolder =
                holder.view.getTag(R.id.item_click_listener_tag_lbc) as? ItemClickListenerHolder<D, V>
            if (clickHolder == null) {
                clickHolder = ItemClickListenerHolder(itemClickListenerProvider, holder, data)
                holder.view.setOnClickListener(clickHolder)
                holder.view.setTag(R.id.item_click_listener_tag_lbc, clickHolder)
            }
            clickHolder.data = data
        }

        if (itemFocusChangedListener != null) {
            var listenerHolder =
                holder.view.getTag(R.id.item_focus_listener_tag_lbc) as? ItemFocusChangedListenerHolder<D, V>
            if (listenerHolder == null) {
                listenerHolder =
                    ItemFocusChangedListenerHolder(itemFocusChangedListenerProvider, holder, data)
                listenerHolder.mChainFocusChangedListener = holder.view.onFocusChangeListener
                holder.view.onFocusChangeListener = listenerHolder
                holder.view.setTag(R.id.item_focus_listener_tag_lbc, listenerHolder)
            }
            listenerHolder.data = data
        }

        if (itemKeyListener != null) {
            var listenerHolder =
                holder.view.getTag(R.id.item_key_listener_tag_lbc) as? ItemKeyListenerHolder<D, V>
            if (listenerHolder == null) {
                listenerHolder =
                    ItemKeyListenerHolder(itemKeyListenerProvider, holder, data)
                holder.view.setOnKeyListener(listenerHolder)
                holder.view.setTag(R.id.item_key_listener_tag_lbc, listenerHolder)
            }
            listenerHolder.data = data
        }
        bindViewHolder(viewHolder, data)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        //nothing ,override for your self
    }

    fun interface OnItemClickListener<D, V : BaseViewHolder> {
        fun onItemClick(viewHolder: V, item: D)
    }

    fun interface OnItemFocusChangedListener<D, V : BaseViewHolder> {
        fun onItemFocusChanged(view: View, hasFocus: Boolean, viewHolder: V, item: D)
    }

    fun interface OnItemKeyListener<D, V : BaseViewHolder> {
        fun onItemKey(v: View?, keyCode: Int, event: KeyEvent?, viewHolder: V, item: D): Boolean
    }

    private interface ItemClickProvider<D, V : BaseViewHolder> {
        fun getItemClickListener(): OnItemClickListener<D, V>?
    }

    private interface ItemFocusChangedListenerProvider<D, V : BaseViewHolder> {
        fun getItemFocusChangedListener(): OnItemFocusChangedListener<D, V>?
    }

    private interface ItemKeyListenerProvider<D, V : BaseViewHolder> {
        fun getItemKeyListener(): OnItemKeyListener<D, V>?
    }

    private class ItemClickListenerHolder<D, V : BaseViewHolder>(
        val provider: ItemClickProvider<D, V>,
        var holder: V,
        var data: D
    ) : View.OnClickListener {
        override fun onClick(v: View?) {
            provider.getItemClickListener()?.onItemClick(holder, data)
        }
    }

    private class ItemFocusChangedListenerHolder<D, V : BaseViewHolder>(
        val provider: ItemFocusChangedListenerProvider<D, V>,
        val viewHolder: V,
        var data: D,
    ) : View.OnFocusChangeListener {

        var mChainFocusChangedListener: View.OnFocusChangeListener? = null

        override fun onFocusChange(v: View, hasFocus: Boolean) {
            provider.getItemFocusChangedListener()
                ?.onItemFocusChanged(v, hasFocus, viewHolder, data)
            mChainFocusChangedListener?.onFocusChange(v, hasFocus)
        }
    }

    private class ItemKeyListenerHolder<D, V : BaseViewHolder>(
        val provider: ItemKeyListenerProvider<D, V>,
        var holder: V,
        var data: D
    ) : View.OnKeyListener {

        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            return provider.getItemKeyListener()?.onItemKey(v, keyCode, event, holder, data) == true
        }
    }

    companion object {

        @JvmStatic
        protected fun createViewHolder(
            parent: ViewGroup,
            layoutId: Int
        ): BaseViewHolder {
            val layoutInflater =
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
            return BaseViewHolder(
                layoutInflater.inflate(
                    layoutId,
                    parent,
                    false
                )
            )
        }
    }
}
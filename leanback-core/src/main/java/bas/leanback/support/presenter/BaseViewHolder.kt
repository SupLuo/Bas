package bas.leanback.support.presenter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.leanback.widget.Presenter
import bas.droid.core.util.layoutInflater
import androidx.core.view.isVisible
import bas.droid.core.view.extensions.isVisibleOrNot
import bas.droid.core.view.extensions.setTextOrGone
import bas.droid.core.view.extensions.setTextOrInvisible
import bas.droid.adapter.imageloader.load

open class BaseViewHolder(view: View) : Presenter.ViewHolder(view) {

    /**
     * Views indexed with their IDs
     */
    private val views: SparseArray<View> = SparseArray()

    constructor(layoutId: Int, parent: ViewGroup) : this(
        parent.context.layoutInflater,
        layoutId,
        parent
    )

    constructor(inflater: LayoutInflater, layoutId: Int, parent: ViewGroup) :
            this(inflater.inflate(layoutId, parent, false))

    open fun <T : View> getView(@IdRes viewId: Int): T {
        val view = getViewOrNull<T>(viewId)
        checkNotNull(view) { "No view found with id $viewId" }
        return view
    }

    @Suppress("UNCHECKED_CAST")
    open fun <T : View> getViewOrNull(@IdRes viewId: Int): T? {
        val view = views.get(viewId)
        if (view == null) {
            this.view.findViewById<T>(viewId)?.let {
                views.put(viewId, it)
                return it
            }
        }
        return view as? T
    }

    fun setText(vid: Int, message: CharSequence?) {
        getViewOrNull<TextView>(vid)?.text = message
    }

    fun loadImage(vid: Int, url: String?, ph: Int) {
        getViewOrNull<ImageView>(vid)?.load(url, ph)
    }

    fun setTextOrGone(vid: Int, message: CharSequence?) {
        getViewOrNull<TextView>(vid)?.setTextOrGone(message)
    }

    fun setTextOrInvisible(vid: Int, message: CharSequence?) {
        getViewOrNull<TextView>(vid)?.setTextOrInvisible(message)
    }

    fun setVisibleOrGone(vid: Int, visible: Boolean) {
        getViewOrNull<View>(vid)?.isVisible = visible
    }

    fun setVisibleOrNot(vid: Int, visible: Boolean) {
        getViewOrNull<View>(vid)?.isVisibleOrNot = visible
    }

}
package bas.leanback.support.presenter

import android.view.View
import android.view.ViewGroup

/**
 * Created by Lucio on 2021/12/9.
 */
abstract class CommonPresenter<D> private constructor(
    val layoutId: Int,
    private val itemViewCreator: ((ViewGroup) -> View)?
) :
    BasePresenter<D, BaseViewHolder>() {

    constructor(layoutId: Int) : this(layoutId, null)

    constructor(itemViewCreator: (ViewGroup) -> View) : this(0, itemViewCreator)

    /**
     * Creates a new [View].
     */
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val holder = if (layoutId > 0) {
            BaseViewHolder(layoutId, parent)
        } else {
            BaseViewHolder(itemViewCreator!!.invoke(parent))
        }
        onViewHolderCreated(holder)
        return holder
    }

    protected open fun onViewHolderCreated(holder: BaseViewHolder) {

    }

}


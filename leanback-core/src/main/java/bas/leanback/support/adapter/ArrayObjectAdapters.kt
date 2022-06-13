@file:JvmName("ArrayObjectAdaptersKt")

package bas.leanback.support.adapter

import androidx.leanback.widget.*
import androidx.leanback.widget.ArrayObjectAdapter
import bas.lib.core.lang.areItemsEqual


fun newArrayObjectAdapter(data: List<*>, presenter: Presenter): ArrayObjectAdapter {
    return ArrayObjectAdapter(presenter).also {
        it.addAll(0, data)
    }
}

fun newArrayObjectAdapter(data: List<*>, presenterSelector: PresenterSelector): ArrayObjectAdapter {
    return ArrayObjectAdapter(presenterSelector).also {
        it.addAll(0, data)
    }
}

fun ArrayObjectAdapter.areContentTheSame(other: ArrayObjectAdapter) :Boolean{
    if (this.size() != other.size())
        return false
    val oldItems = this.unmodifiableList<Any>()
    val newItems = this.unmodifiableList<Any>()
    return oldItems.areItemsEqual(newItems) && presenterSelector.presenters.toList().areItemsEqual(other.presenterSelector.presenters.toList())

}
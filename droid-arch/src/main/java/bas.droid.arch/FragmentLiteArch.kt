package bas.droid.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

@Deprecated("还未想好如何更好的去封装")
open class FragmentLiteArch(@LayoutRes private var contentLayoutId: Int) :
    FragmentArch(contentLayoutId) {

    private val extra_content_layout_id = "_extra_content_layout_id_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(extra_content_layout_id)) {
                contentLayoutId = it.getInt(extra_content_layout_id, contentLayoutId)
            } else {
                it.putInt(extra_content_layout_id, contentLayoutId)
            }
        }
    }

    override fun createContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(contentLayoutId, container, false)
    }

}
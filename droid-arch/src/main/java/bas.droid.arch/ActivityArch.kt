package bas.droid.arch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import bas.droid.core.app.AnonyContext

/**
 * Created by Lucio on 2022/3/23.
 */
abstract class ActivityArch : AppCompatActivity, AnonyContext {
    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override val realCtx: Context
        get() = this
}
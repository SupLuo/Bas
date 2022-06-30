package bas.droid.arch.ui

import androidx.annotation.StringRes
import bas.droid.core.ctxBas

/**
 * @param cancelable 是否可以取消
 */
class AlertException(message: String, val cancelable: Boolean = true) : RuntimeException(message) {
    constructor(@StringRes textId: Int, cancelable: Boolean = true) : this(
        ctxBas.getString(textId),
        cancelable
    )
}
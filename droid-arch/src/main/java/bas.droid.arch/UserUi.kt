package bas.droid.arch

import androidx.lifecycle.LifecycleOwner
import bas.droid.core.app.AnonyContext

/**
 * 用于对Activity和Fragment之间无差别处理逻辑
 */
interface UserUi : LifecycleOwner, AnonyContext{

    fun <T:Any> setTagIfAbsentBas(key: String, newValue: T): T

    fun <T:Any> getTagBas(key: String): T?

    fun <T:Any> getTagBasAndRemove(key: String): T?

    fun removeTagBas(key: String)
}

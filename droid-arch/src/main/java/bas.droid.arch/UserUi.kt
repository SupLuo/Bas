package bas.droid.arch

import androidx.lifecycle.LifecycleOwner
import bas.droid.core.app.AnonyContext

/**
 * 用于对Activity和Fragment之间无差别处理逻辑
 */
interface UserUi : LifecycleOwner, AnonyContext {

    /*目前没有更好的办法：本意是想在UserUi中提供一个容器，用于方便向内存放一些对象，类似ViewModel内部的实现方式*/
    fun <T : Any> setTagIfAbsentBas(key: String, newValue: T): T

    fun <T : Any> getTagBas(key: String): T?

    fun <T : Any> getTagBasAndRemove(key: String): T?

    fun removeTagBas(key: String)
    /*目前没有更好的办法：本意是想在UserUi中提供一个容器，用于方便向内存放一些对象*/

}

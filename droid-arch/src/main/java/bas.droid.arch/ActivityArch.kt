package bas.droid.arch

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Lucio on 2022/3/23.
 */
abstract class ActivityArch : AppCompatActivity, UserUi {

    constructor() : super()

    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override val realCtx: Context
        get() = this


    // Can't use ConcurrentHashMap, because it can lose values on old apis (see b/37042460)
    private val bagOfTags: MutableMap<String, Any> = HashMap()

    override fun <T : Any> setTagIfAbsentBas(key: String, newValue: T): T {
        var previous: T?
        synchronized(bagOfTags) {
            previous = bagOfTags[key] as T?
            if (previous == null) {
                bagOfTags[key] = newValue
            }
        }
        return if (previous == null) newValue else previous!!
    }

    override fun <T : Any> getTagBas(key: String): T? {
        synchronized(bagOfTags) { return bagOfTags[key] as T? }
    }

    override fun <T : Any> getTagBasAndRemove(key: String): T? {
        synchronized(bagOfTags) {
            val value = bagOfTags[key] as T?
            bagOfTags.remove(key)
            return value
        }
    }

    override fun removeTagBas(key: String) {
        synchronized(bagOfTags) {
            bagOfTags.remove(key)
        }
    }
}
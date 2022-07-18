package bas.droid.core.util

import android.os.Build


inline fun <K, V> Map<K, V>.getOrDefaultExt(key: K, defaultValue: V): V {
    return if (Build.VERSION.SDK_INT >= 24) {
        this.getOrDefault(key, defaultValue)
    } else {
        this[key] ?: defaultValue
    }
}

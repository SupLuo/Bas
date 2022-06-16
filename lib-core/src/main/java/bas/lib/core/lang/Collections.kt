/**
 * Created by Lucio on 2020/12/13.
 */
@file:JvmName("CollectionsKt")

package bas.lib.core.lang

import kotlin.collections.isNullOrEmpty as isNullOrEmptyLib

/**
 * For Java
 */
inline fun <T> Collection<T>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

/**
 * 拼接所有非空元素
 */
inline fun <E> MutableCollection<E>.appendAllNotNulls(
    elements: Collection<E>?
): MutableCollection<E> = this.appendAll(elements, true)

/**
 * 拼接元素
 * @param filterNotNulls 是否只过滤非空元素，即是否不添加null元素
 */
inline fun <E> MutableCollection<E>.appendAll(
    elements: Collection<E>?,
    filterNotNulls: Boolean = false
): MutableCollection<E> {
    CollectionUtils.mergeTo(this, elements, filterNotNulls)
    return this
}

/**
 *
 * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
 */
inline fun <E> MutableCollection<E>.addAllNotNulls(
    elements: Collection<E>?
): Boolean {
    if (!elements.isNullOrEmptyLib()) {
        return this.addAll(elements)
    }
    return false
}

inline fun <E> Collection<E>?.areItemsEqual(other: Collection<E>?) =
    CollectionUtils.areItemsEqual(this, other)

/**
 * 累加 elements.hashCode()
 */
inline fun <T> Collection<T>?.hashCodeAccumulate(): Int {
    var result = 0
    this?.forEach {
        result = 31 * result + it.hashCode()
    }
    return result
}
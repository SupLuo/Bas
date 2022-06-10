package bas.leanback.support.presenter

import android.os.Bundle

abstract class BasePayloadsPresenter<Data, VHolder : BaseViewHolder> :
    BasePresenter<Data, VHolder>() {

    final override fun onBindViewHolder(
        viewHolder: ViewHolder?,
        item: Any?,
        payloads: MutableList<Any>?
    ) {
        val first = payloads?.firstOrNull() ?: return super.onBindViewHolder(
            viewHolder,
            item,
            payloads
        )

        if (first is Payloads<*>) {
            when (first) {
                is Payloads.FlagPayloads -> {
                    onBindViewHolderFlagPayloads(viewHolder as VHolder, item as Data, first)
                }
                is Payloads.BundlePayloads -> {
                    onBindViewHolderBundlePayloads(viewHolder as VHolder, item as Data, first)
                }
                else -> {
                    return super.onBindViewHolder(
                        viewHolder,
                        item,
                        payloads
                    )
                }
            }
        } else {
            return super.onBindViewHolder(
                viewHolder,
                item,
                payloads
            )
        }
    }

    protected open fun onBindViewHolderFlagPayloads(
        viewHolder: VHolder,
        data: Data,
        payloads: Payloads.FlagPayloads
    ) {
        onBindViewHolder(viewHolder, data)
    }

    protected open fun onBindViewHolderBundlePayloads(
        viewHolder: VHolder,
        data: Data,
        payloads: Payloads.BundlePayloads
    ) {
        onBindViewHolder(viewHolder, data)
    }

    abstract fun getChangePayloads(old: Data, new: Data): Payloads<*>

    sealed class Payloads<T>(val data: T) {

        /**
         * 标记
         */
        sealed class FlagPayloads(data: String) : Payloads<String>(data) {

            class Custom(data: String) : FlagPayloads(data)

            object BASIC : FlagPayloads("basic")

            object SPEC : FlagPayloads("special")
        }

        class BundlePayloads(data: Bundle) : Payloads<Bundle>(data)

        class Custom<T>(data: T) : Payloads<T>(data)

        /**
         * 刷新所有
         */
        object ALL : Payloads<Unit>(Unit)
    }
}

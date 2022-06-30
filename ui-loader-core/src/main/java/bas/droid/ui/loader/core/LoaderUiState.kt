package bas.droid.ui.loader.core


sealed interface LoaderUiState {

    companion object {

        @JvmField
        val LOADING = Loading("")

        /**
         * 用于内容视图但不关心数据场景的快速使用
         */
        @JvmField
        val CONTENT = Content(Unit)

        /**
         * 用于内容视图为空场景的快速使用
         */
        @JvmField
        val EMPTY = Content(null)

        @JvmStatic
        fun empty(message: String) = Content(null, message)
    }

    /**
     * 懒惰状态:不知道该干嘛，，保持现状？
     */
    object LAZY : LoaderUiState {
        override val extra: Any? = null
    }

    val extra: Any?

    /**
     * 是否为内容视图:内容视图又分为数据内容视图和空数据内容视图
     */
    val isContentState: Boolean get() = this is Content<*>

    /**
     * 是否为数据内容视图
     */
    val isDataState: Boolean get() = this is Content<*> && this.data != null

    /**
     * 是否为空内容视图
     */
    val isEmptyState: Boolean get() = (this == EMPTY) || (this is Content<*> && this.data == null)

    /**
     * loading状态
     */
    data class Loading @JvmOverloads constructor(
        val message: String,
        override val extra: Any? = null
    ) : LoaderUiState

    /**
     * 数据状态
     */
    data class Content<T> @JvmOverloads constructor(
        val data: T,
        override val extra: Any? = null
    ) : LoaderUiState {

        companion object {

            fun <T> empty(extra: Any? = null): Content<T?> {
                return Content(null, extra)
            }

            fun <T> data(data: T?, extra: Any? = null): Content<T?> {
                return Content(data, extra)
            }
        }
    }

//    /**
//     * 数据状态：但不附加数据的情况，充当标志的情况
//     */
//    object CONTENT : LoaderUiState {
//        override val extra: Any? = null
//    }

    /**
     * 错误状态
     */
    data class Error @JvmOverloads constructor(
        val error: Throwable,
        val message: String = error.message.orEmpty(),
        override val extra: Any? = null
    ) : LoaderUiState

//    data class MessageUiState(
//        val id: Long = UUID.randomUUID().mostSignificantBits,
//        val message: String,
//        override val extra: Any? = null
//    ) : UiState


}


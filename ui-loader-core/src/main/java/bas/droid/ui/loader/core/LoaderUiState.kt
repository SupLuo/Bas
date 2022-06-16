package bas.droid.ui.loader.core


sealed interface LoaderUiState {

    val extra: Any?

    /**
     * 懒惰状态:不知道该干嘛，，保持现状？
     */
    object LAZY : LoaderUiState {
        override val extra: Any? = null
    }

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
    ) : LoaderUiState

    /**
     * 数据状态：但不附加数据的情况，充当标志的情况
     */
    object CONTENT : LoaderUiState {
        override val extra: Any? = null
    }

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
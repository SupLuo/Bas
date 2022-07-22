package bas.droid.arch.udf

import bas.lib.core.coroutines.flatReduce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch


/**
 * Ui意图
 */
interface UiIntent

/**
 * Ui状态
 */
interface UiState


/**
 * 可部分变化的数据结果
 * @param R 指新的UiState类型
 */
interface PartialUiState<R> {
    /**
     * 通过旧的状态产生新的状态
     * @param old 旧的UiState
     * @return 新的UiState
     */
    fun reduce(old: R): R

}
//
//fun <Intent : UiIntent, State : UiState> Intent.toPartialFlow(reducer: (Intent) -> PartialUiState<State>): Flow<PartialUiState<State>> =
//    flowOf(this).map { intent ->
//        reducer(intent)
//    }

fun <Intent : UiIntent, State : UiState> UDFFlow(
    initial: State,
    factory: (Intent) -> Flow<PartialUiState<State>>
): UDF<Intent, State> {
    return UDFImpl(initial, factory)
}


interface UDF<Intent : UiIntent, State : UiState> {

    /**
     * 发送意图
     */
    fun sendIntent(scope: CoroutineScope, intent: Intent)

    /**
     * 提供的UiStateFlow
     */
    val uiStateFlow: Flow<State>
}

/**
 * 用户发起一个意图，这个意图经过执行之后会产生一个结果，称这个结果为PartialResult（即意图转换得到的数据可能只是最终所需UiState的部分值），
 * 然后再将PartialResult结合老的的UiState生成新的UiState，然后将新的值发射出去
 *
 * intent(用户的意图) -> PartialFlow（部分结果流）->(+ old UiState)->new Ui State
 */
private class UDFImpl<Intent : UiIntent, State : UiState>(
    initial: State,
    private val factory: (Intent) -> Flow<PartialUiState<State>>
) : UDF<Intent, State> {

    private val _uiIntents = MutableSharedFlow<Intent>()

    /**
     * 转换之后的UiState
     */
    override val uiStateFlow: Flow<State> = _uiIntents.flatMapConcat { intent ->
        factory.invoke(intent)
    }.flatReduce(initial) { old, partialResult ->
        partialResult.reduce(old)
    }

    /**
     * 发送意图
     */
    override fun sendIntent(scope: CoroutineScope, intent: Intent) {
        scope.launch {
            _uiIntents.emit(intent)
        }
    }

}

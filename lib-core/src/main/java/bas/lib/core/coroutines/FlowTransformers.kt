package bas.lib.core.coroutines

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*


fun <T, R> Flow<T>.flatReduce(initial: R, reduce: suspend (old: R, value: T) -> R): Flow<R> =
    ReduceFlowImpl(initial, this, reduce)


/**
 * 用于请求本地数据和服务器数据的情况
 * 如果发射顺序是 local 然后是 remote，则会向下发射 local和remote的结果
 * 如果发射顺序先是remote，则只会向下发射remote的结果。
 * @param localFlow 本地流
 * @param remoteFlow 远程流
 */
@OptIn(FlowPreview::class)
fun <R> flowOfMeditor(localFlow: Flow<R>, remoteFlow: Flow<R>): Flow<R> =
    flowOf(localFlow.map {
        AbortResult<R>(it, false)
    }, remoteFlow.map {
        AbortResult(it, true)
    }).flattenMerge()
        .transformWhile {
            emit(it.data)
            !it.isAbort
        }

/**
 * 用于包装数据结果
 * @param data 真实数据
 * @param isAbort 是否中断流
 */
private data class AbortResult<T>(val data: T, val isAbort: Boolean)


/**
 * 结合就值产新值
 */
private class ReduceFlowImpl<T, R>(
    private var initial: R,
    private val upstream: Flow<T>,
    @JvmField val reducer: suspend (R, T) -> R
) : Flow<R> {
    override suspend fun collect(collector: FlowCollector<R>) {
        upstream.collect { value ->
            initial = reducer(initial, value)
            collector.emit(initial)
        }
    }
}
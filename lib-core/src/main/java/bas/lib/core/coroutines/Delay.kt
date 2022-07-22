package bas.lib.core.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 限流：在指定时间内只响应最开始的那个数据流
 * @param thresholdMillis 限流时间，即在该时间内产生的数据只发射最开始那个数据
 */
fun <T> Flow<T>.throttleFirst(thresholdMillis: Long): Flow<T> = flow {
    var lastEmitTime = 0L // 上次发射数据的时间
    // 收集数据
    collect { upstream ->
        // 当前时间
        val currentTime = System.currentTimeMillis()
        println("current timestamp= $currentTime lastEmitTime=${lastEmitTime}")
        // 时间差超过阈值则发送数据并记录时间
        if (currentTime - lastEmitTime >= thresholdMillis) {
            lastEmitTime = currentTime
            emit(upstream)
        }
    }
}

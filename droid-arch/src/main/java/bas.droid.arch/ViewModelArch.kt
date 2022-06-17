package bas.droid.arch

import androidx.lifecycle.ViewModel
import bas.lib.core.coroutines.ControlledRunner
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Lucio on 2022/3/23.
 */

open class ViewModelArch : ViewModel() {

    private val joinPreviousRunners: ConcurrentHashMap<String, ControlledRunner<Any?>> =
        ConcurrentHashMap()

    private val cancelPreviousRunners: ConcurrentHashMap<String, ControlledRunner<Any?>> =
        ConcurrentHashMap()

    /**
     * 执行或加入之前启动的[key]相同的任务：即如果之前已经启动了同名Key的任务，并且未执行完成，则当前认为会忽略，并
     */
    suspend fun <T> launchOrJoinPrevious(key: String, func: suspend () -> T): T {
        val runner = synchronized(joinPreviousRunners) {
            joinPreviousRunners.getOrPut(key) {
                ControlledRunner()
            }
        }
        return runner.joinPreviousOrRun(func) as T
    }


    /**
     * 执行并取消之前启动的[key]相同的任务
     */
    @Synchronized
    suspend fun <T> launchAndCancelPrevious(key: String, func: suspend () -> T): T {
        val runner = synchronized(cancelPreviousRunners) {
            cancelPreviousRunners.getOrPut(key) {
                ControlledRunner()
            }
        }
        return runner.cancelPreviousThenRun(func) as T
    }
}
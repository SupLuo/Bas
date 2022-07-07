/**
 * 采用随机算法，在指定的最大时间和最大进度内模拟进度变化
 */
package bas.droid.core.content

import androidx.annotation.IntRange
import bas.droid.core.util.Logger
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.random.Random

/**
 * 执行耗时任务的时候同步进行伪进度条
 * @param timeMax 伪进度的最大预期执行时间，即进度在[timeMax]时间之后会达到[progressMax]值
 * @param progressMax 伪进度能够达到的最大进度值
 * @param count 回调多少次，即刷新[count]次界面进度值
 * @param progressCallback 进度回调(已切回UI线程执行)
 * @param task 工作任务
 */
suspend fun <T> invokeTaskWithProgressFaker(
    timeMax: Long = 10 * 1000,
    @IntRange(from = 0, to = 100)
    progressMax: Int = 99,
    @IntRange(from = 2)
    count: Int = calculateDispatchCount(timeMax),
    progressCallback: ProgressFakerTaskCallback,
    task: suspend () -> T
): T {
    return coroutineScope {
        val workTask = async(Dispatchers.IO) {
            task()
        }
        val progressFakerTask = async(Dispatchers.Default) {
            invokeProgressFaker(timeMax, progressMax, count) { duration, progress ->
                withContext(Dispatchers.Main) {
                    progressCallback.onProgress(duration, progress)
                }
            }
        }
        val results = workTask.await()
        if (!progressFakerTask.isCompleted) {
            progressFakerTask.cancel(CancellationException("数据已获取成功"))
        }
        results
    }
}

/**
 * 该方法只是使用模板，不建议直接使用
 * @see invokeProgressFaker
 * @see invokeTaskWithProgressFaker
 */
@Deprecated(
    "该方法只是使用模板，不建议直接使用", replaceWith = ReplaceWith(
        "async(Dispatchers.Default) {\n" +
                "        invokeProgressFaker(timeMax, progressMax, count, callback)\n" +
                "    }", "bas.droid.core.content.invokeProgressFaker"
    )
)
suspend inline fun CoroutineScope.invokeProgressFakerAsync(
    timeMax: Long = 10 * 1000,
    @IntRange(from = 0, to = 100)
    progressMax: Int = 99,
    @IntRange(from = 2)
    count: Int = calculateDispatchCount(timeMax),
    callback: ProgressFakerTaskCallback
): Deferred<Unit> {
    return this.async(Dispatchers.Default) {
        invokeProgressFaker(12000, 95, 24, callback)
    }
}

/**
 * 执行伪进度任务（用于创建一个假的进度条任务）
 * 建议：为避免阻塞调用线程，建议使用async运行本方法，并[CoroutineContext]为[Dispatchers.Default]
 *
 * 借鉴红包算法，只不过本方法是两个两个维度的计算
 * 参考：https://blog.csdn.net/weixin_36586564/article/details/106783652
 *
 * @param timeMax 指定经过多少时间达到指定的[progressMax]值 ： 类似于红包金额
 * @param progressMax 模拟进度的最大值 ： 类似于红包金额
 * @param count 计算的次数：类似于抢红包参与的人数,默认一秒2次(最少2次)
 */
suspend inline fun CoroutineScope.invokeProgressFaker(
    timeMax: Long = 10 * 1000,
    @IntRange(from = 0, to = 100)
    progressMax: Int = 99,
    @IntRange(from = 2)
    count: Int = calculateDispatchCount(timeMax),
    callback: ProgressFakerTaskCallback
) {
    Logger.d("ProgressFaker", "执行进度模拟")
    val timeDivides = doubleAverageStrategy(timeMax.toInt(), count)
    val progressDivides = doubleAverageStrategy(progressMax, count)
    Logger.d("ProgressFaker", "timeDivides=$timeDivides \nprogressDivides=$progressDivides")

    var castProgress = 0
    var castTime = 0

    timeDivides.forEachIndexed { index, time ->
        if (isActive) {
            delay(time.toLong())
            castTime += time
            castProgress += progressDivides[index]
            callback.onProgress(castTime, castProgress)
            Logger.d(
                "ProgressFaker",
                "onProgress(duration=$castTime,progress=$castProgress)"
            )
        } else {
            Logger.d(
                "ProgressFaker",
                "inActived"
            )
        }
    }
}

/**
 * 伪进度回调
 */
fun interface ProgressFakerTaskCallback {

    /**
     * @param duration 执行时间
     * @param progress 当前进度
     */
    suspend fun onProgress(duration: Int, progress: Int)
}

/**
 * 二倍均值法
 * 参考：https://blog.csdn.net/weixin_36586564/article/details/106783652
 * @param total 总的值
 * @param num 次数
 */
fun doubleAverageStrategy(@IntRange(from = 1) total: Int, @IntRange(from = 1) num: Int): List<Int> {
    val results = mutableListOf<Int>()
    var restValue = total
    var restNum = num
    for (i in 0 until num - 1) {
        val value = Random.nextInt(restValue / restNum * 2 - 1) + 1
        restValue -= value
        restNum--
        results.add(value)
    }
    //添加余下的值
    results.add(restValue)
    return results
}

/**
 * 根据时间计算分发进度的次数：一秒2次，最少2次
 */
fun calculateDispatchCount(timeMax: Long): Int {
    return max((timeMax / 1000 * 2).toInt(), 2)
}
package bas.lib.core.date

import java.text.SimpleDateFormat
import java.util.*
import javax.management.timer.Timer.ONE_DAY

/**
 * 日期格式化接口
 */
interface DateFormatUseCase {
    fun format(date: Date): String

    companion object {

        @JvmStatic
        fun newSimple(pattern: String, locale: Locale = Locale.getDefault()): DateFormatUseCase {
            return SimpleDateFormatUseCase(pattern, locale)
        }
    }
}

/**
 *
 * 日期格式化用例
 */
class SimpleDateFormatUseCase(pattern: String, locale: Locale = Locale.getDefault()) :
    DateFormatUseCase {

    private val formatter: SimpleDateFormat = SimpleDateFormat(pattern, locale)

    override fun format(date: Date): String {
        return formatter.format(date)
    }
}

/**
 * 默认3分钟内属于“刚刚”
 */
private const val DEFAULT_JUST_NOW_TIME = 180000L

private const val DEFAULT_JUST_NOW_TEXT = "刚刚"

interface JustNowFormatUseCase : DateFormatUseCase {
    fun isJustNowTime(date: Date): Boolean
}

class DefaultJustNowFormatUseCase(
    private val justNowText: String = DEFAULT_JUST_NOW_TEXT,
    private val justNowLimitTime: Long = DEFAULT_JUST_NOW_TIME
) : JustNowFormatUseCase {
    override fun isJustNowTime(date: Date): Boolean {
        val deltaTime = System.currentTimeMillis() - date.time
        return deltaTime < justNowLimitTime
    }

    override fun format(date: Date): String {
        return justNowText
    }
}

/**
 * 友好日期格式化用例
 */
class FriendlyDateFormatUseCase(
    private val locale: Locale = Locale.getDefault(),
    private val justNowFormat: JustNowFormatUseCase = DefaultJustNowFormatUseCase(),
    private val todayFormat: DateFormatUseCase = SimpleDateFormatUseCase("HH:mm", locale),
    private val yesterdayFormat: DateFormatUseCase = SimpleDateFormatUseCase("昨天 HH:mm", locale),
    private val beforeYesterdayFormat: DateFormatUseCase = SimpleDateFormatUseCase(
        "前天 HH:mm",
        locale
    ),
    private val sameYearFormat: DateFormatUseCase = SimpleDateFormatUseCase(
        "MM-dd HH:mm:ss",
        locale
    ),
    private val otherFormat: DateFormatUseCase = SimpleDateFormatUseCase(
        "yyyy-MM-dd HH:mm:ss",
        locale
    )
) : DateFormatUseCase {

    override fun format(date: Date): String {
        return if (justNowFormat.isJustNowTime(date)) { // 十分钟内
            justNowFormat.format(date)
        } else {
            val nowCal = Calendar.getInstance()
            nowCal.time = Date()
            val dateCal = Calendar.getInstance()
            dateCal.time = date
            //相差时间
            val deltaTime = nowCal.timeInMillis - dateCal.timeInMillis
            val isSameYear = nowCal[Calendar.YEAR] == dateCal[Calendar.YEAR]
            val deltaDays = if (isSameYear) {//属于同一年,则可以直接用
                (nowCal[Calendar.DAY_OF_YEAR] - dateCal[Calendar.DAY_OF_YEAR]).toLong()
            } else {
                deltaTime / ONE_DAY_TIME + 1//本身用ceil函数向上取整即可，但是直接+1基本也不会有问题，不存在两个时间恰好相等
            }

            // 注：格式化字符串存在区分大小写
            // 对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
            // yyyy代表年份，如“2010”；dd代表天，如“25”
            if (deltaDays == 0L) {//属于当天时间
                todayFormat.format(date)
            } else if (deltaDays == 1L) {
                yesterdayFormat.format(date)
            } else if (deltaDays == 2L) {
                beforeYesterdayFormat.format(date)
            } else {
                if (isSameYear) {
                    sameYearFormat.format(date)
                } else {
                    otherFormat.format(date)
                }
            }
        }
    }

}
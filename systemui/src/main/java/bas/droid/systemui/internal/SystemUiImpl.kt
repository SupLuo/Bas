/**
 * Created by Lucio on 2019/6/6.
 */

package bas.droid.systemui.internal

import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import bas.droid.systemui.SystemUi

/**
 * 混合颜色
 * @param color1
 * @param color2
 * @param ratio 如果比率为0，则使用[color1],如果比率为0.5,则均匀混合两种颜色，如果比率为1，则使用[color2]
 */
internal fun blendColor(
    @ColorInt color1: Int,
    @ColorInt color2: Int,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Int {
    return blendARGB(color1, color2, ratio)
}

/**
 * 为了不添加core-ktx的依赖，因此复制了方法[androidx.core.graphics.ColorUtils.blendARGB]
 */
@ColorInt
fun blendARGB(
    @ColorInt color1: Int, @ColorInt color2: Int,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Int {
    val inverseRatio = 1 - ratio
    val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
    val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
    val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
    val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
    return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
}

internal val impl: SystemUi by lazy {
    val sdkInt = Build.VERSION.SDK_INT
    when {
        sdkInt >= 23 -> SystemUi23()
        sdkInt >= 21 -> SystemUi21()
        sdkInt >= 19 -> SystemUi19()
        else -> SystemUiDefault()
    }
}
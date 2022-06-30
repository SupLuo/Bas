package bas.droid.widget

import android.content.Context
import android.graphics.Canvas
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by Lucio on 2021/6/30.
 * 传统跑马灯（基于系统Marquee方式）
 */
class MarqueeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var isMarqueeEnabled: Boolean = false

    init {
        //配置跑马灯必须属性
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.MARQUEE//跑马灯效果
        marqueeRepeatLimit = -1      //无限循环
    }

    /**
     * 设置多条文本（采用拼接的形式）
     */
    fun setTexts(contents: List<String>?, separator: String = "                            ") {
        text = contents?.filter {
            it.isNotEmpty()
        } ?.joinToString(separator).orEmpty()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        //确保设置文本之后跑马灯能动：目前有个问题就是初次运行，跑马灯没动
        if (isMarqueeEnabled) {
            startScroll()
        }
    }

    fun startScroll() {
        enableMarquee()
    }

    fun stopScroll() {
        disableMarquee()
    }

    @Deprecated("该方法已被去掉，避免对外修改属性")
    override fun setSelected(selected: Boolean) {
        Log.w("MarqueeTextView", "setSelected method will be ignored.")
    }

    override fun isFocused(): Boolean {
        return isMarqueeEnabled || super.isFocused()
    }

//    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
//        if (focused)
//            super.onFocusChanged(focused, direction, previouslyFocusedRect)
//    }

////    不改变window focus，避免当前界面不可见的时候还在进行跑马灯
//    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
//        if (hasWindowFocus)
//            super.onWindowFocusChanged(hasWindowFocus)
//    }

    private fun disableMarquee() {
        isMarqueeEnabled = false
        super.setSelected(isMarqueeEnabled)
    }

    /**
     * 启用跑马灯
     */
    private fun enableMarquee() {
        if (isSelected) {//如果之前已选中，则先设置为false，不然会导致原来值也为true不会引起跑马灯动画
            super.setSelected(false)
        }
        isMarqueeEnabled = true
        super.setSelected(isMarqueeEnabled)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d("跑马灯", "正在绘制")
    }

}

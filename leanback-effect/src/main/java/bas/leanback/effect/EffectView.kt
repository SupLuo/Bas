package bas.leanback.effect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

internal class EffectView @JvmOverloads constructor(
    val params: EffectParams,
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val strokePaint = Paint()
    private val strokeRectF = RectF()
    private val strokePath: Path = Path()

    private val shadowPaint = Paint()
    private val shadowRectF = RectF()
    private val shadowPath: Path = Path()

    //呼吸动画
    private val breatheAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "alpha", 1f, 0.2f, 1f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = params.breatheDuration.toLong()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            startDelay = params.shimmerDelay.toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    alpha = 1f
                }
            })
        }
    }

    init {
        // 需禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        if (shadowEnabled) {
            this.shadowPaint.isAntiAlias = true
            this.shadowPaint.maskFilter =
                BlurMaskFilter(params.shadowWidth, BlurMaskFilter.Blur.OUTER)
            this.shadowPaint.strokeWidth = 1f
            this.shadowPaint.color = params.shadowColor
        }

        if (strokeEnabled) {
            this.strokePaint.isAntiAlias = true
            this.strokePaint.color = params.strokeColor
            this.strokePaint.strokeWidth = params.strokeWidth
            this.strokePaint.style = Paint.Style.STROKE
            this.strokePaint.maskFilter = BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL)
        }

        visibility = GONE
    }

    private val shadowEnabled: Boolean get() = params.shadowWidth > 0
    private val strokeEnabled: Boolean get() = params.strokeWidth > 0

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        updateShadowParamsOnSizeChanged(width, height, oldw, oldh)
        updateStrokeParamsOnSizeChanged(width, height, oldw, oldh)
    }

    private fun updateShadowParamsOnSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        if (!shadowEnabled)
            return

        val newLeft = params.shadowWidth
        val newTop = params.shadowWidth
        val newRight = width - params.shadowWidth
        val newBottom = height - params.shadowWidth

        if (newLeft == shadowRectF.left && newTop == shadowRectF.top && newRight == shadowRectF.right && newBottom == shadowRectF.bottom)
            return

        shadowPath.reset()
        shadowRectF.set(newLeft, newTop, newRight, newBottom)
        //必须使用这种方式，否则在某些情况下会出现中间有个阴影色的色块
        if (params.isRoundedShape) {
            shadowPath.addRoundRect(shadowRectF, params.cornerRadius, Path.Direction.CW)
        } else {
            shadowPath.addRoundRect(shadowRectF,0f,0f,Path.Direction.CW)
        }
    }

    private fun updateStrokeParamsOnSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        if (!strokeEnabled)
            return

        //如果带有圆角，避免圆角处与stroke之间有间隙，因此用整个stroke宽度作为偏移，否则只需用半个
        val offset =
            params.strokeWidthHalf//if(params.isRoundedShape) params.strokeWidth else params.strokeWidthHalf

        //todo 很奇怪，不管怎么计算，绘制的路径都有些许偏差
        val newLeft = paddingLeft + params.shadowWidth
        val newTop = paddingTop + params.shadowWidth
        val newRight =width - paddingRight - params.shadowWidth
        val newBottom = height - paddingBottom - params.shadowWidth

        if (newLeft == strokeRectF.left && newTop == strokeRectF.top && newRight == strokeRectF.right && newBottom == strokeRectF.bottom)
            return

        strokePath.reset()
        strokeRectF.set(newLeft, newTop, newRight, newBottom)
        //必须使用这种方式，否则在某些情况下会出现中间有个阴影色的色块
        if (params.isRoundedShape) {
            strokePath.addRoundRect(strokeRectF, params.cornerRadius, Path.Direction.CW)
        } else {
            strokePath.addRect(strokeRectF, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (shadowEnabled) {
            canvas.save()
            canvas.drawPath(this.shadowPath, this.shadowPaint)
            canvas.restore()
        }
        if (strokeEnabled) {
            canvas.save()
            canvas.drawPath(this.strokePath, this.strokePaint)
            canvas.restore()
        }
    }

    fun startAnimation() {
        visibility = VISIBLE
        if (params.breatheEnabled) {
            if (breatheAnim.isStarted)
                breatheAnim.cancel()
            breatheAnim.start()
        } else {
            alpha = 1f
        }
    }

    fun stopAnimation() {
        visibility = GONE
        if (params.breatheEnabled) {
            breatheAnim.cancel()
        } else {
            breatheAnim.end()
        }
    }

}
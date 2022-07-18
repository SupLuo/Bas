package bas.droid.adapter.mediaplayer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import bas.droid.adapter.mediaplayer.qn.QNVideoView
import bas.droid.adapter.mediaplayer.sys.SysVideoView
import bas.droid.adapter.mediaplayer.tencent.TXVideoView

/**
 * Created by Lucio on 2021/9/22.
 * 可以配置/切换播放器内核的播放器
 *
 * @see MediaPlayer
 * @see R.styleable.ConfigurablePlayerView_kernel 在xml中通过该属性可以配置默认加载的播放器内核，lazy属性表示不加载默认播放器内核
 * @see [ConfigurableVideoView.setKernelVideoView] 根据类型动态指定播放器内核
 */
class ConfigurableVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractVideoView(context, attrs, defStyleAttr), MediaPlayer {

    private var kernelView: AbstractVideoView? = null

    @KernelType
    private var kernelType: Int = KernelType.SYS

    private val listeners = mutableListOf<MediaPlayer.PlayerListener>()

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ConfigurablePlayerView,
            defStyleAttr,
            0
        )
        kernelType = a.getInt(R.styleable.ConfigurablePlayerView_kernel, KernelType.SYS)
        a.recycle()

        if (kernelType != KernelType.LAZY) {
            addKernelVideoView(kernelType)
        }
    }

    fun requireQNPlayer(): QNVideoView {
        return kernelView as QNVideoView
    }

    fun requireSysPlayer(): SysVideoView {
        return kernelView as SysVideoView
    }

    fun requireTXPlayer(): TXVideoView {
        return kernelView as TXVideoView
    }

    //提醒kernel video view不可用
    private fun warnKernelVideoViewValidation(actionName: String) {
        if (kernelView == null) {
            logw("real video view is null,action '$actionName' is ignore.")
        }
    }

    /**
     * 动态切换播放器内核
     */
    fun setKernelVideoView(@KernelType type: Int) {
        if (type == this.kernelType && kernelView != null) {
            logi("switch kernel video view fail,the type value is same with current type value${type}.")
            return
        }
        logi("当前使用播放器类型=${type}.")
        resetKernelVideoView()
        addKernelVideoView(type)
    }

    //添加内部播放器
    private fun addKernelVideoView(@KernelType type: Int) {
        val kernelView = MediaPlayerAdapter.createVideoView(context, type)
        kernelView.id = R.id.video_view_id_bas
        addView(kernelView, generateDefaultKernelViewLayoutParams(context))
        this.kernelType = type
        this.kernelView = kernelView
        //重新绑定当前已经设置的监听器：避免切换播放器内核之后之前设置的监听器无效
        if (listeners.isNotEmpty()) {
            listeners.forEach {
                kernelView.addPlayerListener(it)
            }
        }
    }

    //重置内部播放器
    private fun resetKernelVideoView() {
        kernelView?.let {
            logi("resetKernelVideoView: 释放之前的播放器")
            it.release()
            //从parent view group 中移除自己
            (it.parent as? ViewGroup)?.removeView(it)

            listeners.forEach { listener ->
                it.removePlayerListener(listener)
            }
        }
        kernelView = null
        kernelType = KernelType.LAZY
    }

    val videoView: View? get() = findViewById(R.id.video_view_id_bas)

    /**
     * 腾讯播放器
     */
    val txVideoView: TXVideoView? get() = videoView as? TXVideoView

    /**
     * 系统播放器
     */
    val sysVideoView: SysVideoView? get() = videoView as? SysVideoView

    /**
     * 七牛播放器
     */
    val qnVideoView: QNVideoView? get() = videoView as? QNVideoView

    /**
     * 设置播放数据
     */
    override fun setDataSource(url: String) {
        warnKernelVideoViewValidation("setDataSource(url)")
        kernelView?.setDataSource(url)
    }

    /**
     * 设置播放数据
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, seekTimeMs: Int) {
        warnKernelVideoViewValidation("setDataSource(url,seekTimeMs)")
        kernelView?.setDataSource(url, seekTimeMs)
    }

    /**
     * 设置播放数据
     * @param headers 播放设置的headers
     */
    override fun setDataSource(url: String, headers: Map<String, String>) {
        warnKernelVideoViewValidation("setDataSource(url,headers)")
        kernelView?.setDataSource(url, headers)
    }

    /**
     * 设置播放数据
     * @param headers 播放设置的headers
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, headers: Map<String, String>, seekTimeMs: Int) {
        warnKernelVideoViewValidation("setDataSource(url,headers,seekTimeMs)")
        kernelView?.setDataSource(url, headers, seekTimeMs)
    }

    /**
     * 是否正在播放
     */
    override fun isPlaying(): Boolean {
        warnKernelVideoViewValidation("isPlaying()")
        return kernelView?.isPlaying() == true
    }

    /**
     * 从指定位置开始播放
     * @param positionMs ms
     */
    override fun seekTo(positionMs: Int) {
        warnKernelVideoViewValidation("seekTo()")
        kernelView?.seekTo(positionMs)
    }

    /**
     * 获取持续时间
     * @return ms
     */
    override fun getDuration(): Int {
        warnKernelVideoViewValidation("getDuration()")
        return kernelView?.getDuration() ?: 0
    }

    /**
     * 获取当前播放位置
     * @return ms
     */
    override fun getCurrentPosition(): Int {
        warnKernelVideoViewValidation("getCurrentPosition()")
        return kernelView?.getCurrentPosition() ?: 0
    }

    /**
     * 开始播放
     */
    override fun start() {
        warnKernelVideoViewValidation("start()")
        kernelView?.start()
    }

    /**
     * 暂停播放
     */
    override fun pause() {
        warnKernelVideoViewValidation("pause()")
        kernelView?.pause()
    }

    override fun resume() {
        kernelView?.resume()
    }

    /**
     * 停止播放
     */
    override fun stop() {
        warnKernelVideoViewValidation("stop()")
        kernelView?.stop()
    }

    /**
     * 释放播放器
     */
    override fun release() {
        warnKernelVideoViewValidation("release()")
        kernelView?.release()
    }

    /**
     * 缓冲百分比
     * @return ms
     */
    override fun getBufferPercentage(): Int {
        warnKernelVideoViewValidation("getBufferPercentage()")
        return kernelView?.getBufferPercentage() ?: 0
    }

    /**
     * 添加播放监听
     */
    override fun addPlayerListener(listener: MediaPlayer.PlayerListener) {
        warnKernelVideoViewValidation("addPlayerListener()")
        kernelView?.addPlayerListener(listener)
        listeners.add(listener)
    }

    /**
     * 移除播放监听
     */
    override fun removePlayerListener(listener: MediaPlayer.PlayerListener) {
        warnKernelVideoViewValidation("removePlayerListener()")
        kernelView?.removePlayerListener(listener)
        listeners.remove(listener)
    }
}
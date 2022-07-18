package bas.droid.adapter.mediaplayer.qn

import android.content.Context
import android.util.AttributeSet
import android.view.View
import bas.droid.adapter.mediaplayer.*
import com.pili.pldroid.player.*
import com.pili.pldroid.player.widget.PLVideoView
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by Lucio on 2021/9/19.
 */
class QNVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractVideoView(
    context,
    attrs,
    defStyleAttr
) {

    val kernelView: PLVideoView

    private var defaultAVOptions: AVOptions = QNPlayer.VideoPlayOptions()

    private var listeners: CopyOnWriteArrayList<MediaPlayer.PlayerListener> =
        CopyOnWriteArrayList<MediaPlayer.PlayerListener>()

    private val internalPreparedListener = PLOnPreparedListener {
        listeners.forEach {
            it.onPlayPrepared()
        }
    }

    private val internalCompleteListener = PLOnCompletionListener {
        listeners.forEach {
            it.onPlayEnd()
        }
    }

    private val internalErrorListener = PLOnErrorListener { what, obj ->
        //忽略拖动失败异常
        if (what == PLOnErrorListener.ERROR_CODE_SEEK_FAILED)
            return@PLOnErrorListener true
        listeners.forEach {
            it.onPlayError(QNPlayerError.new(context, what))
        }
        true
    }

    private val internalInfoListener = PLOnInfoListener { what, extra, obj ->
        when (what) {
            PLOnInfoListener.MEDIA_INFO_BUFFERING_START -> {
                listeners.forEach {
                    it.onPlayBuffering()
                }
            }
            PLOnInfoListener.MEDIA_INFO_BUFFERING_END -> {
                listeners.forEach {
                    it.onPlayBufferingEnd()
                }
            }

            PLOnInfoListener.MEDIA_INFO_VIDEO_RENDERING_START -> {
                listeners.forEach {
                    it.onPlayStart()
                }
            }
        }
    }

    init {
        kernelView =
            if (MediaPlayerAdapter.isLeanbackMode) createKernelViewFromXmlForLeanback() else PLVideoView(
                context
            )

        kernelView.id = R.id.video_view_kernel_id_bas


        addView(kernelView, generateDefaultKernelViewLayoutParams(context, attrs, defStyleAttr))
        kernelView.setOnPreparedListener(internalPreparedListener)
        kernelView.setOnCompletionListener(internalCompleteListener)
        kernelView.setOnErrorListener(internalErrorListener)
        kernelView.setOnInfoListener(internalInfoListener)
        setDefaultConfig()
    }

    protected open fun createKernelViewFromXmlForLeanback(): PLVideoView {
        val view = context.layoutInflater.inflate(
            R.layout.bas_video_view_kernel_qn,
            this,
            false
        ) as PLVideoView
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        view.visibility = View.VISIBLE
        return view
    }

    /**
     * 请在开始播放之前配置
     */
    fun setAVOptions(options: AVOptions) {
        defaultAVOptions = options
//        if(kernelView.isPlaying){
//            kernelView.stopPlayback()
//        }
        kernelView.setAVOptions(options)
    }

    private fun setDefaultConfig() {
        //默认铺满全屏幕
        kernelView.displayAspectRatio = PLVideoView.ASPECT_RATIO_FIT_PARENT
    }

    /**
     * 设置播放数据
     */
    override fun setDataSource(url: String) {
        val options = defaultAVOptions
        options.setInteger(AVOptions.KEY_START_POSITION, 0)
        kernelView.setAVOptions(options)
        kernelView.setVideoPath(url)
    }

    /**
     * 设置播放数据
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, seekTimeMs: Int) {
        val options = defaultAVOptions
        options.setInteger(AVOptions.KEY_START_POSITION, seekTimeMs)
        kernelView.setAVOptions(options)
        kernelView.setVideoPath(url)
    }

    override fun setDataSource(url: String, headers: Map<String, String>) {
        val options = defaultAVOptions
        options.setInteger(AVOptions.KEY_START_POSITION, 0)
        kernelView.setAVOptions(options)
        kernelView.setVideoPath(url, headers)
    }

    /**
     * 设置播放数据
     * @param headers 播放设置的headers
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, headers: Map<String, String>, seekTimeMs: Int) {
        val options = defaultAVOptions
        options.setInteger(AVOptions.KEY_START_POSITION, seekTimeMs)
        kernelView.setAVOptions(options)
        kernelView.setVideoPath(url, headers)
    }

    /**
     * 是否正在播放
     */
    override fun isPlaying(): Boolean {
        return kernelView.isPlaying
    }

    /**
     * 从指定位置开始播放
     * @param positionMs ms
     */
    override fun seekTo(positionMs: Int) {
        kernelView.seekTo(positionMs.toLong())
    }

    /**
     * 获取持续时间
     * @return ms
     */
    override fun getDuration(): Int {
        return kernelView.duration.toInt()
    }

    /**
     * 获取当前播放位置
     * @return ms
     */
    override fun getCurrentPosition(): Int {
        return kernelView.currentPosition.toInt()
    }

    /**
     * 开始播放
     */
    override fun start() {
        kernelView.start()
    }

    /**
     * 暂停播放
     */
    override fun pause() {
        kernelView.pause()
    }

    override fun resume() {
        kernelView.start()
    }

    /**
     * 停止播放
     */
    override fun stop() {
        kernelView.pause()
    }

    /**
     * 释放播放器
     */
    override fun release() {
        kernelView.stopPlayback()
        listeners.clear()
    }

    /**
     * 缓冲百分比
     * @return ms
     */
    override fun getBufferPercentage(): Int {
        return kernelView.bufferPercentage
    }

    override fun addPlayerListener(listener: MediaPlayer.PlayerListener) {
        listeners.add(listener)
    }

    override fun removePlayerListener(listener: MediaPlayer.PlayerListener) {
        listeners.remove(listener)
    }

}
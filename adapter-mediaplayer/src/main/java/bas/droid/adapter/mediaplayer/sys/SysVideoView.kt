package bas.droid.adapter.mediaplayer.sys

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.annotation.RequiresApi
import bas.droid.adapter.mediaplayer.*
import java.util.concurrent.CopyOnWriteArrayList
import android.media.MediaPlayer as SysMediaPlayer

/**
 * Created by Lucio on 2021/4/21.

 */
class SysVideoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AbstractVideoView(context, attrs, defStyleAttr), MediaPlayer {

    val kernelView: VideoView

    private var listeners: CopyOnWriteArrayList<MediaPlayer.PlayerListener> =
        CopyOnWriteArrayList<MediaPlayer.PlayerListener>()

    private var loadingAssistPosition: Int = -1

    private val internalPreparedListener = SysMediaPlayer.OnPreparedListener {
        log("OnPreparedListener")
        performLoadingAssist()
        listeners.forEach {
            it.onPlayPrepared()
        }
    }

    private val internalCompleteListener = SysMediaPlayer.OnCompletionListener {
        log("OnCompletionListener")
        cancelLoadingAssist()
        listeners.forEach {
            it.onPlayEnd()
        }
    }

    private val internalErrorListener = SysMediaPlayer.OnErrorListener { _, what, extra ->
        log("OnErrorListener(what=$what extra=$extra)")
        cancelLoadingAssist()
        listeners.forEach {
            it.onPlayError(SysPlayerError.new(what, extra))
        }
        true
    }

    private val internalInfoListener = SysMediaPlayer.OnInfoListener { _, what, extra ->
        log("OnInfoListener(what=$what extra=$extra)")
        when (what) {
            SysMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                listeners.forEach {
                    it.onPlayBuffering()
                }
            }
            SysMediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                listeners.forEach {
                    it.onPlayBufferingEnd()
                }
            }
            SysMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                listeners.forEach {
                    it.onPlayStart()
                }
            }
        }
        false
    }

    init {
        kernelView =
            if (MediaPlayerAdapter.isLeanbackMode) createKernelViewFromXmlForLeanback() else VideoViewCompat(
                context
            )
        kernelView.id = R.id.video_view_kernel_id_bas
        addView(kernelView, generateDefaultKernelViewLayoutParams(context, attrs, defStyleAttr))
        kernelView.setOnCompletionListener(internalCompleteListener)
        kernelView.setOnErrorListener(internalErrorListener)
        kernelView.setOnPreparedListener(internalPreparedListener)
        kernelView.setOnInfoListener(internalInfoListener)
    }

    private fun log(msg: String) {
        Log.d("SysVideoView",msg)
    }

    protected open fun createKernelViewFromXmlForLeanback(): VideoView {
        val view = context.layoutInflater.inflate(
            R.layout.bas_video_view_kernel_sys,
            this,
            false
        ) as VideoView
        view.isFocusable = false
        view.isFocusableInTouchMode = false
        view.visibility = View.VISIBLE
        return view
    }

    private val loadingAssistRunnable = object : Runnable {
        override fun run() {
            val duration = this@SysVideoView.kernelView.currentPosition
            if (this@SysVideoView.kernelView.isPlaying && loadingAssistPosition == duration) {
                listeners.forEach {
                    it.onPlayBuffering()
                }
            } else {
                listeners.forEach {
                    it.onPlayBufferingEnd()
                }
            }
            loadingAssistPosition = duration
            postDelayed(this, 1000)
        }
    }

    /**
     * 执行加载状态辅助操作：某些盒子loading状态回调之后，进入播放状态，
     * 但是没有任何回调，或者盒子进入缓冲或缓冲结束也不会有任何回调
     */
    private fun performLoadingAssist() {
        cancelLoadingAssist()
        post(loadingAssistRunnable)
    }

    fun cancelLoadingAssist() {
        removeCallbacks(loadingAssistRunnable)
    }

    override fun setDataSource(url: String) {
        kernelView.setVideoPath(url)
    }

    /**
     * 设置播放数据
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, seekTimeMs: Int) {
        kernelView.setVideoPath(url)
        seekTo(seekTimeMs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setDataSource(url: String, headers: Map<String, String>) {
        kernelView.setVideoURI(Uri.parse(url), headers)
    }

    /**
     * 设置播放数据
     * @param headers 播放设置的headers
     * @param seekTimeMs 开始播放时间，单位ms
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun setDataSource(url: String, headers: Map<String, String>, seekTimeMs: Int) {
        kernelView.setVideoURI(Uri.parse(url), headers)
        seekTo(seekTimeMs)
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
        kernelView.seekTo(positionMs)
    }

    /**
     * 获取持续时间
     * @return ms
     */
    override fun getDuration(): Int {
        return kernelView.duration
    }

    /**
     * 获取当前播放位置
     * @return ms
     */
    override fun getCurrentPosition(): Int {
        return kernelView.currentPosition
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
        if (kernelView.canPause())
            kernelView.pause()
    }

    override fun resume() {
        kernelView.start()
    }

    /**
     * 停止播放
     */
    override fun stop() {
        kernelView.stopPlayback()
    }

    /**
     * 释放播放器
     */
    override fun release() {
        //取消辅助回调
        cancelLoadingAssist()
        kernelView.stopPlayback()
        //清除所有对外的回调
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
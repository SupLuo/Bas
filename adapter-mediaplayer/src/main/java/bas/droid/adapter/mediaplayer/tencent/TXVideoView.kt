package bas.droid.adapter.mediaplayer.tencent

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.IntRange
import bas.droid.adapter.mediaplayer.AbstractVideoView
import bas.droid.adapter.mediaplayer.MediaPlayer
import bas.droid.adapter.mediaplayer.R
import com.tencent.rtmp.ITXVodPlayListener
import com.tencent.rtmp.TXLiveConstants
import com.tencent.rtmp.TXVodPlayConfig
import com.tencent.rtmp.TXVodPlayer
import com.tencent.rtmp.ui.TXCloudVideoView
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Created by Lucio on 2021/9/20.
 * "版本：9.1.10566 部分盒子上，在ViewPager2中使用，当第一个Item视频正常播放结束之后切换到下一个，整个盒子卡死甚至花屏"
 */
class TXVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbstractVideoView(context, attrs, defStyleAttr), MediaPlayer {

    private val kernelView: TXCloudVideoView
    private val kernel: TXVodPlayer
    private var playUrl: String? = null

    private val listeners: CopyOnWriteArrayList<MediaPlayer.PlayerListener> =
        CopyOnWriteArrayList<MediaPlayer.PlayerListener>()

    private var customVodListener: ITXVodPlayListener? = null

    private val vodListener = object : ITXVodPlayListener {
        override fun onPlayEvent(player: TXVodPlayer, event: Int, param: Bundle?) {
            logi("onPlayEvent(event=$event)")
            onKernelEvent(player, event, param)
            customVodListener?.onPlayEvent(player, event, param)
        }

        override fun onNetStatus(player: TXVodPlayer, param: Bundle?) {
            logi("onNetStatus")
            customVodListener?.onNetStatus(player, param)
        }
    }

    init {
        kernelView = TXCloudVideoView(context)
        kernelView.visibility = View.GONE
        kernelView.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION)
        //判断当前是否为横屏模式
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            kernelView.setRenderRotation(TXLiveConstants.RENDER_ROTATION_LANDSCAPE)
        } else {
            kernelView.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT)
        }

        kernelView.id = R.id.video_view_kernel_id_bas
        addView(kernelView, generateDefaultKernelViewLayoutParams(context, attrs, defStyleAttr))
        kernel = TXVodPlayer(context).apply {
            //默认开启硬解码，解码失败会切换成软解
            enableHardwareDecode(true)
            setVodListener(vodListener)
            setPlayerView(kernelView)
        }
        logi("init complete")
    }

    private fun logi(msg: String) {
        Log.i("TXMediaPlayer", msg)
    }

    private fun logw(msg: String) {
        Log.w("TXMediaPlayer", msg)
    }

    /**
     * 播放器事件，参考连接：https://cloud.tencent.com/document/product/454/17246
     *
     * note：
     * 判断直播是否结束：基于各种标准的实现原理不同，很多直播流通常没有结束事件（2006）抛出，此时可预期的表现是：主播结束推流后，SDK 会很快发现数据流拉取失败（WARNING_RECONNECT），然后开始重试，直至三次重试失败后抛出 PLAY_ERR_NET_DISCONNECT 事件。所以2006和 -2301都要判断，用来作为直播结束的判定事件。
     * 不要在收到 PLAY_LOADING 后隐藏播放画面：因为 PLAY_LOADING 到 PLAY_BEGIN 的时间长短是不确定的，可能是5s也可能是5ms，有些客户考虑在 LOADING 时隐藏画面， BEGIN 时显示画面，会造成严重的画面闪烁（尤其是直播场景下）。推荐的做法是在视频播放画面上叠加一个半透明的加载动画。
     */
    private fun onKernelEvent(player: TXVodPlayer, event: Int, param: Bundle?) {
        logi("onEvent event=${event}")
        when (event) {
            TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED -> {
                listeners.forEach {
                    //播放器已准备完成，可以播放
                    it.onPlayPrepared()
                }
            }
            TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME -> {
                //网络接收到首个可渲染的视频数据包（IDR）
            }
            TXLiveConstants.PLAY_EVT_PLAY_PROGRESS -> {
//                //视频播放进度，会通知当前播放进度、加载进度 和总体时长
//                // 加载进度, 单位是毫秒
//                val duration_ms = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS)
//                mLoadBar.setProgress(duration_ms)
//
//                // 播放进度, 单位是毫秒
//                val progress_ms = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS)
//                mSeekBar.setProgress(progress_ms)
//
//                // 视频总长, 单位是毫秒
//                val duration_ms = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS)
//                // 可以用于设置时长显示等等
            }
            TXLiveConstants.PLAY_EVT_PLAY_BEGIN -> {
                //视频播放开始，如果有转菊花什么的这个时候该停了
                listeners.forEach {
                    it.onPlayStart()
                }
            }
            TXLiveConstants.PLAY_EVT_PLAY_LOADING -> {
                //视频播放 loading，如果能够恢复，之后会有 LOADING_END 事件
                listeners.forEach {
                    it.onPlayBuffering()
                }
            }
            TXLiveConstants.PLAY_EVT_VOD_LOADING_END -> {
                //视频播放 loading 结束，视频继续播放
                listeners.forEach {
                    it.onPlayBufferingEnd()
                }
            }
            TXLiveConstants.PLAY_EVT_PLAY_END -> {
                //视频播放结束
                listeners.forEach {
                    it.onPlayEnd()
                }
                onPlayCompleteCompat()
            }
            TXLiveConstants.PLAY_ERR_NET_DISCONNECT -> {
                onPlayCompleteCompat()
                //网络断连,且经多次重连亦不能恢复,更多重试请自行重启播放
                listeners.forEach {
                    it.onPlayError(TXPlayerError.new(context, event))
                }
            }
            TXLiveConstants.PLAY_ERR_GET_RTMP_ACC_URL_FAIL -> {
                //获取加速拉流失败，这是由于您传给liveplayer的加速流地址中没有携带txTime和txSecret签名，或者是签名计算的不对。出现这个错误时，liveplayer会放弃拉取加速流转而拉取 CDN 上的视频流，从而导致延迟很大。
                //需要注意：只有 RTMP 协议的播放地址才支持低延时加速。
            }
            TXLiveConstants.PLAY_ERR_HLS_KEY,
            TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND,
            TXLiveConstants.PLAY_ERR_HEVC_DECODE_FAIL,
            TXLiveConstants.PLAY_ERR_GET_PLAYINFO_FAIL,
            TXLiveConstants.PLAY_ERR_STREAM_SWITCH_FAIL -> {
                onPlayCompleteCompat()
                //HLS 解密 key 获取失败
                listeners.forEach {
                    it.onPlayError(TXPlayerError.new(context, event))
                }
            }
        }
    }

    /**
     *  版本：9.1.10566 部分盒子上，在ViewPager2中使用，当第一个Item视频正常播放结束之后切换到下一个，整个盒子卡死甚至花屏
     *  提交工单跟腾讯客户沟通之后，对方给出的回复：您好，这个日志前面看了，调用流程是没问题的，和后台确认了，结论也是说应该是机器性能不一致引起的。
     *  两个机器虽然Ram都是1G，但他们的系统优化层都可能会影响到，sdk不会自动去回收资源，需要您这边手动调用stopPlay去释放资源解决这个问题的
     *
     *  @note 版本：9.1.10566 部分盒子上，在ViewPager2中使用，当第一个Item视频正常播放结束之后切换到下一个，整个盒子卡死甚至花屏
     */
    private fun onPlayCompleteCompat() {
        logi("执行部分盒子卡死花屏的兼容代码")
        kernel.stopPlay(true)
    }

    private fun DefaultPlayConfig(): TXVodPlayConfig {
        return TXVodPlayConfig()
    }

    /**
     * 设置播放数据
     */
    override fun setDataSource(url: String) {
        setKernelParams(url)
    }

    /**
     * 设置播放数据
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, seekTimeMs: Int) {
        setKernelParams(url, seekTimeMs)
    }

    override fun setDataSource(url: String, headers: Map<String, String>) {
        setKernelParams(url, headers = headers)
    }

    /**
     * 设置播放数据
     * @param headers 播放设置的headers
     * @param seekTimeMs 开始播放时间，单位ms
     */
    override fun setDataSource(url: String, headers: Map<String, String>, seekTimeMs: Int) {
        setKernelParams(url, seekTimeMs, headers)
    }

    private fun setKernelParams(
        url: String,
        startTime: Int = 0,
        headers: Map<String, String>? = null,
        autoPlay: Boolean = false
    ) {
        this.playUrl = url
        kernel.also {
            val config = DefaultPlayConfig()
            if (headers != null) {
                config.setHeaders(headers)
            }
            it.setConfig(config)
            it.setStartTime(startTime.toFloat())
            it.setAutoPlay(autoPlay)
            it.startPlay(url)
        }
        logi("setKernelParams \nurl=${url} \nstartTime=$startTime \nheaders=${headers.toString()} \nautoPlay=$autoPlay")
    }

    /**
     * 是否正在播放
     */
    override fun isPlaying(): Boolean {
        return kernel.isPlaying
    }

    /**
     * 从指定位置开始播放
     * @param positionMs ms
     */
    override fun seekTo(positionMs: Int) {
        /*kernal.seek()
        跳转到视频流指定时间点. 可实现视频快进,快退,进度条跳转等功能.
        参数
        time	视频流时间点,单位为秒
        time	视频流时间点,小数点后为毫秒
        */
        kernel.seek(positionMs / 1000f)
    }

    /**
     * 获取持续时间
     * @return ms
     */
    override fun getDuration(): Int {
        //获取总时长 单位s
        return (kernel.duration * 1000).toInt()
    }

    /**
     * 获取当前播放位置
     * @return ms
     */
    override fun getCurrentPosition(): Int {
//     kernal.currentPlaybackTime   获取当前播放位置 单位秒
        return (kernel.currentPlaybackTime * 1000).toInt()
    }

    /**
     * 开始播放
     */
    override fun start() {
        kernel.resume()
    }

    /**
     * 暂停播放
     */
    override fun pause() {
        kernel.pause()
    }

    override fun resume() {
        kernel.resume()
    }

    /**
     * 停止播放
     */
    override fun stop() {
        stop(true)
    }

    fun stop(isNeedClearLastImg: Boolean) {
        kernel.stopPlay(isNeedClearLastImg)
    }

    /**
     * 释放播放器
     */
    override fun release() {
        kernel.stopPlay(true)
        kernelView.onDestroy()
        //清除所有对外的回调
        listeners.clear()
    }

    /**
     * 缓冲百分比
     * @return ms
     */
    @IntRange(from = 0, to = 100)
    override fun getBufferPercentage(): Int {
        return (kernel.bufferDuration * 100f / kernel.duration.coerceAtLeast(1f)).toInt()
    }

    override fun addPlayerListener(listener: MediaPlayer.PlayerListener) {
        listeners.add(listener)
    }

    override fun removePlayerListener(listener: MediaPlayer.PlayerListener) {
        listeners.remove(listener)
    }

    /**
     * 开启硬解码
     */
    fun enableHardwareDecode() {
        if (isPlaying() && !this.playUrl.isNullOrEmpty()) {
            val url = this.playUrl!!
            val position = this.getCurrentPosition()
            kernel.stopPlay(true)
            kernel.enableHardwareDecode(true)
            setDataSource(url, position)
        } else {
            kernel.stopPlay(true)
            kernel.enableHardwareDecode(true)
        }
    }

    /**
     * 对外提供设置内核事件监听：避免直接访问kernel设置vodlistener，会导致内部设置的vodlistener失效
     */
    fun setVodListener(listener: ITXVodPlayListener?) {
        customVodListener = listener
    }
}
package bas.droid.core.os

import android.os.Debug
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.Printer
import bas.droid.core.BuildConfig

/**
 * 用于Looper 消息执行监控
 */
class LooperMessagePrinter(private val tag: String) : Printer {

    companion object {

        /**
         * Android每秒满帧为60帧（现在也有120帧的设备了）
         */
        private const val FULL_PER_SECOND_FRAMES = 60

        /**
         * 一帧的时间，大致为16ms
         */
        private const val ONE_FRAME_TIME = 1000 / FULL_PER_SECOND_FRAMES

        /**
         * 消息的开始标记
         */
        private const val START_TAG = ">>>>> Dispatching"

        /**
         * 消息的结束标记
         */
        private const val FINISHED_TAG = "<<<<< Finished"

        //界面刷新 消息
        private const val FRAME_UPDATE_MESSAGE_TAG =
            "Choreographer"  //"android.view.Choreographer\$FrameHandler"

        private const val ACTIVITY_THREAD_TAG = "ActivityThread\$H" //"ActivityThread\$H"

        @JvmStatic
        fun apply(tag: String = "LooperMessagePrinter", looper: Looper = Looper.getMainLooper()) {
            looper.setMessageLogging(LooperMessagePrinter(tag))
        }
    }

    private var startMilliseconds = 0L

    /**
     * 设置是否可用
     */
    var enable: Boolean = true

    /**
     * 调试信息开关
     */
    var debuggable: Boolean = BuildConfig.DEBUG

    /**
     *
     *  Loop中执行的代码
     *  // This must be in a local variable, in case a UI event sets the logger

    final Printer logging = me.mLogging;
    if (logging != null) {
    logging.println(">>>>> Dispatching to " + msg.target + " " +
    msg.callback + ": " + msg.what);
    }
    ...
    msg.target.dispatchMessage(msg);
    ...
    if (logging != null) {
    logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
    }

     */

    override fun println(msg: String?) {
        if (!enable)
            return

        if (msg.isNullOrEmpty()
            || msg.contains(FRAME_UPDATE_MESSAGE_TAG)
            || msg.contains(ACTIVITY_THREAD_TAG)
        )
            return

        if (msg.startsWith(START_TAG)) {
            if (debuggable) {
                Log.d(tag, msg)
            }
            startMilliseconds = SystemClock.elapsedRealtime()
        } else {


            //消息执行消耗时间
            val usedMillis = SystemClock.elapsedRealtime() - startMilliseconds
            if (usedMillis < ONE_FRAME_TIME) {
                if (debuggable) {
                    Log.d(tag, msg)
                }
                return
            } else if (usedMillis < 48) {
                if (debuggable) {
                    Log.d(tag, msg)
                }
            } else if (usedMillis < 200) {
                Log.w(tag, "$msg\n$usedMillis ms used")
            } else {
                Log.e(tag, "$msg\n[Caution Runnable Block]:$usedMillis ms used")
                report(msg, usedMillis)
            }
        }
    }

    private fun report(msg: String, usedMillis: Long) {
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger() || debuggable) return
        //todo
    }

}
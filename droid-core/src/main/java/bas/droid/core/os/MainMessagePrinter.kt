package bas.droid.core.os

import android.os.Looper
import android.util.Printer

class MainMessagePrinter : Printer {

    override fun println(msg: String?) {
//        if(msg.isNullOrEmpty() || msg.contains("Choreographer")
//            || msg.contains("ActivityThread\$H"))
//            return
//
//        if (msg.startsWith(">>>>> Dispatching")) {
//            mLastMillis = SystemClock.elapsedRealtime()
//            mTimesPerSeconds++
//            if ( mTimesPerSeconds > 100) {
//                report(msg, 0, mTimesPerSeconds)
//            }
//            if (mLastMillis - mLastSeconds > 1000) {
//                mLastSeconds = mLastMillis
//                mTimesPerSeconds = 0
//            }
//        } else {// <<<<< Finished
//            val now = SystemClock.elapsedRealtime()
//            val usedMillis = now - mLastMillis
//            if (usedMillis < 16L) return
//
//            if (usedMillis > 48) {
//                Log.w(TAG, "$usedMillis ms used for $msg, $mLastSeconds")
//            } else {
//                Log.i(TAG, "$usedMillis ms used for $msg, $mLastSeconds")
//            }
//
//            if (usedMillis > 500L) {
//                report(msg, usedMillis, mTimesPerSeconds)
//                mTimesPerSeconds = 0
//            }
//        }
//
        kotlin.io.println("[MainMessage]:$msg")
    }

    companion object {

        @JvmStatic
        fun apply(looper: Looper = Looper.getMainLooper()) {
            looper.setMessageLogging(MainMessagePrinter())
        }
    }
}
package bas.droid.core.location

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.annotation.DrawableRes


inline val Context.locationManager: LocationManager get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager


/**
 *  定位服务是否可用
 */
inline fun Context.isLocationServiceEnable(): Boolean {
    return locationManager.getProviders(true).isNotEmpty()
}


/**
 * GPS定位开关是否已打开
 */
inline fun Context.isGPSLocationEnable(): Boolean {
    return this.applicationContext.locationManager.isGPSLocationEnable()
}

/**
 * 网络定位开关是否已打开
 */
inline fun Context.isNetworkLocationEnable(): Boolean {
    return this.applicationContext.locationManager.isNetworkLocationEnable()
}

/**
 * 定位服务前台通知
 *
 * 在 28 设备以上最好开启前台服务：https://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/android8-notice
 *
 */
fun LocationForegroundNotification(
    context: Context,
    channelId: String,
    activityIntentForClick: Intent,
    contentTitle: String = "正在进行后台定位",
    contentText: String = "后台定位通知",
    @DrawableRes icon: Int
): Notification {
    //开启前台定位服务：

    //获取一个Notification构造器
    val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= 26) {
        Notification.Builder(context.applicationContext, channelId)
    } else {
        Notification.Builder(context.applicationContext)
    }
    builder.setContentIntent(
        PendingIntent.getActivity(
            context,
            0,
            activityIntentForClick,
            0
        )
    )// 设置PendingIntent

        .setContentTitle(contentTitle) // 设置下拉列表里的标题
        .setSmallIcon(icon) // 设置状态栏内的小图标
        .setContentText(contentText) // 设置上下文内容
        .setAutoCancel(true)
        .setWhen(System.currentTimeMillis()) // 设置该通知发生的时间


    return builder.build().also {
        it.defaults = Notification.DEFAULT_SOUND //设置为默认的声音
    }
//    // 调起前台定位
//    mLocClient.enableLocInForeground(1001, notification)
//
//    //停止前台定位服务：
//    mLocClient.disableLocInForeground(true) // 关闭前台定位，同时移除通知栏
}

/**
 * GPS定位开关是否已打开
 */
inline fun LocationManager.isGPSLocationEnable(): Boolean {
    return isProviderEnabled(LocationManager.GPS_PROVIDER)
}

/**
 * 网络定位开关是否已打开
 */
inline fun LocationManager.isNetworkLocationEnable(): Boolean {
    return isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


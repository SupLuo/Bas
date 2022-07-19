@file:JvmName("ApplicationInfoKt")

package bas.droid.core.util

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


/**
 * 当前应用是否处于前台
 * @Note 据百度上某些网友说在某些机型上，始终返回的是ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
 * @link 在api14上建议使用此方法[halo.android.content.AppState]
 * "Note: this method is only intended for debugging or building a user-facing process management UI."
 */
fun Context.isAppForeground(): Boolean {
    val processes = activityManager?.runningAppProcesses ?: return false
    val pkgName = packageName
    processes.forEach {
        if (it.processName == pkgName)
            return it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
    }
    return false
}

/**
 * 判断指定服务是否正在后台运行
 * @param
 */
fun Context.isServiceRunning(serviceClass: Class<out Service>): Boolean {
    return isServiceRunning(serviceClass.name)
}

/**
 * 判断指定className的service是否正在后台运行
 * @param className 服务的class name，可以使用Class.getName获取
 * @return
 */
fun Context.isServiceRunning(className: String): Boolean {
    val serviceList = activityManager?.getRunningServices(100) ?: return false
    if (serviceList.isNullOrEmpty())
        return false

    serviceList.forEach {
        if (it.service.className == className)
            return true
    }
    return false
}

/**
 * 获取清单文件中指定的meta-data
 * 本方法只支持基础类型值的获取
 * @param key     MetaData对应的Key
 * @param default 默认值，即metadata中无对应key对应的值时返回的值
 */
fun <Value> Context.getMetaData(key: String, default: Value): Value {
    val applicationInfo =
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    val metaData = applicationInfo.metaData ?: return default
    val result = when (default) {
        is Byte -> {
            metaData.getByte(key, default)
        }
        is Char -> {
            metaData.getChar(key, default)
        }
        is Int -> {
            metaData.getInt(key, default)
        }
        is Long -> {
            metaData.getLong(key, default)
        }
        is Float -> {
            metaData.getFloat(key, default)
        }
        is Double -> {
            metaData.getDouble(key, default)
        }
        is String -> {
            metaData.getString(key, default)
        }
        is Boolean -> {
            metaData.getBoolean(key, default)
        }
        else -> {
            throw IllegalArgumentException("只支持基础类型的获取")
        }
    }
    return result as Value
}

/**
 * 获取清单文件中指定的meta-data
 * @param key     MetaData对应的Key
 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
 */
inline fun Context.getMetaData(key: String): String? {
    val applicationInfo =
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
    return applicationInfo.metaData?.getString(key)
}


/**
 * 获得渠道号
 * @param channelKey 指定渠道Key
 * @return
 */
inline fun Context.getChannelNo(channelKey: String): String? = getMetaData(channelKey)

/**
 * 获取程序版本名称
 * @return
 */
inline fun Context.getVersionName(): String {
    val packInfo = packageManager.getPackageInfo(packageName, 0)//0代表是获取版本信息
    return packInfo.versionName
}

/**
 * 获取版本号
 * @return
 */
inline fun Context.getVersionCode(): Long {
    val packInfo = packageManager.getPackageInfo(packageName, 0)
    return if (Build.VERSION.SDK_INT >= 28) {
        packInfo.longVersionCode
    } else {
        packInfo.versionCode.toLong()
    }
}

/**
 * 获取应用程序名称
 */
inline fun Context.getAppName(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val labelRes = packageInfo.applicationInfo.labelRes
        resources.getString(labelRes)
    } catch (e: Throwable) {
        e.printStackTrace()
        ""
    }
}

/**
 * 获取程序第一次安装时间
 */
inline fun Context.getFirstInstallTime(): Long {
    val packInfo = packageManager.getPackageInfo(packageName, 0)//0代表是获取版本信息
    return packInfo.firstInstallTime
}

/**
 * 获取程序上次更新时间
 */
inline fun Context.getLastUpdateTime(): Long {
    val packInfo = packageManager.getPackageInfo(packageName, 0)//0代表是获取版本信息
    return packInfo.lastUpdateTime
}

/**
 * 获取目标版本
 */
inline fun Context.getTargetSdkVersion(ctx: Context): Int {
    val packInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
    return packInfo.applicationInfo.targetSdkVersion
}




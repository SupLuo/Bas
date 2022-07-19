/**
 * Created by Lucio on 2021/9/16.
 */
@file:JvmName("DroidCoreKt")

package bas.droid.core

import android.app.Application
import android.content.pm.ApplicationInfo
import bas.droid.core.app.UI_MODE_TYPE_UNDEFINED
import bas.droid.core.app.currentUiModeType
import bas.droid.core.app.internal.ApplicationManagerImpl
import bas.droid.core.net.URLCoderDroid
import bas.droid.core.ui.droidExceptionHandler
import bas.droid.core.util.Logger
import bas.droid.core.util.getMetaData
import bas.lib.core.base64Decoder
import bas.lib.core.base64Encoder
import bas.lib.core.exceptionHandler
import bas.lib.core.urlCoder

lateinit var ctxBas: Application

val appInstance: Application
    get() = ctxBas

/**
 * 初始化Core Lib
 * @param debuggable 是否开启调试模式 默认根据编译变量确定
 */
@Synchronized
fun initDroidCore(
    app: Application,
    debuggable: Boolean = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
) {
    bas.lib.core.debuggable = debuggable
    if (isInit) {
        return
    }

    isInit = true
    ctxBas = app

    currentUiModeType = app.getMetaData(app.getString(R.string.bas_ui_mode), UI_MODE_TYPE_UNDEFINED)
    Logger.d("DroidCore", "currentUiModeType=$currentUiModeType")

    //设置URL编码
    urlCoder = URLCoderDroid
    //设置base64编解码
    val base64Coder = bas.droid.core.util.Base64(android.util.Base64.NO_WRAP)
    base64Encoder = base64Coder
    base64Decoder = base64Coder
    //设置异常处理器
    exceptionHandler = droidExceptionHandler
    //初始化应用管理器
    ApplicationManagerImpl.init(app)
}

internal var isInit: Boolean = false

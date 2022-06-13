package bas.droid.core.app.internal

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import bas.droid.core.app.ActivityStack
import bas.droid.core.app.ApplicationManager
import bas.lib.core.debuggable

/**
 * Created by Lucio on 18/3/16.
 * 确定App是前台还是后台状态
 */
@TargetApi(14)
internal object ApplicationManagerImpl : ApplicationManager {

    override var isDebuggable: Boolean
        set(value) {
            debuggable = value
        }
        get() = debuggable

    private const val TAG: String = "AppManagerImpl"

    //进入暂停状态时，延迟时间检测app的状态
    private const val PAUSE_STATE_CHECK_DELAY_TIME: Long = 300L

    /**
     * 是否正在暂停（用于确定A 启动 B ，A进入pause ，B进入create等中间这段时间能够更正确的表述app状态）
     */
    private var isPausing = false

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var checkRunnable: Runnable? = null

    private var stateListeners: MutableList<ApplicationManager.OnAppStateChangedListener>? =
        null

    /**
     * 当前app是否在前台运行
     *
     * @return
     */
    override var isForeground = false
        private set

    override val activityStack: ActivityStack = ActivityStackImpl()

    /**
     * 初始化
     */
    fun init(app: Application) {
        //避免重复绑定
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        //绑定回调
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }

    /**
     * 重置
     */
    fun reset(app: Application) {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        isForeground = false
        isPausing = false
        activityStack.clear()
        stateListeners?.clear()
        checkRunnable = null
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 绑定App运行状态改变监听
     */
    override fun registerAppStateChangedListener(listener: ApplicationManager.OnAppStateChangedListener) {
        if (stateListeners == null) {
            stateListeners = mutableListOf(listener)
        } else {
            stateListeners!!.add(listener)
        }
    }

    /**
     * 解绑App运行状态改变监听
     */
    override fun unregisterAppStateChangedListener(listener: ApplicationManager.OnAppStateChangedListener) {
        stateListeners?.remove(listener)
    }

    private val activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks =
        object : Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d(TAG,"$activity onActivityCreated")
                activityStack.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d(TAG,"$activity onActivityStarted")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d(TAG,"$activity onActivityResumed")
                isPausing = false
                val isBackground = !isForeground
                isForeground = true

                checkRunnable?.let {
                    handler.removeCallbacks(it)
                }

                if (isBackground) {
                    Log.d(TAG, "background became foreground")
                    stateListeners?.forEach {
                        try {
                            it.onAppBecameForeground()
                        } catch (e: Throwable) {
                            Log.d(TAG, "listener throw exception", e)
                        }
                    }
                } else {
                    Log.d(TAG, "still foreground")
                }
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d(TAG,"$activity onActivityPaused")
                isPausing = true
                checkRunnable?.let {
                    handler.removeCallbacks(it)
                }
                checkRunnable = Runnable {
                    if (isForeground && isPausing) {
                        isForeground = false
                        Log.d(TAG, "became background")
                        if (stateListeners == null)
                            return@Runnable
                        for (listener in stateListeners!!) {
                            try {
                                listener.onAppBecameBackground()
                            } catch (e: Throwable) {
                                e.printStackTrace()
                                Log.e(TAG, "listener throw exception!", e)
                            }
                        }
                    } else {
                        Log.d(TAG, "still foreground")
                    }
                }
                handler.postDelayed(
                    checkRunnable!!,
                    PAUSE_STATE_CHECK_DELAY_TIME
                )
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d(TAG,"$activity onActivityStopped")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d(TAG,"$activity onActivitySaveInstanceState")
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d(TAG,"$activity onActivityDestroyed")
                activityStack.remove(activity)
            }
        }
}

package bas.droid.net.retrofit

import kotlin.reflect.KClass

/**
 * Created by Lucio on 2018/7/12.
 */

class ApiServiceLazy<ApiService : Any>(
    private val clz: KClass<ApiService>,
    private val factory: ApiServiceFactory
) : Lazy<ApiService> {

    @Volatile
    private var _service: ApiService? = null

    private var _url: String? = null

    override val value: ApiService
        get() {
            val currentUrl = factory.baseUrl
            if (currentUrl != _url) {//不可用
                //检查service是否可用：如果当前service的url与client的url不一致，则不可用
                _service = null
            }
            val service = _service
            if (service == null) {
                synchronized(ApiServiceLazy::class.java) {
                    if (_service == null) {
                        _url = currentUrl
                        _service = factory.createService(clz)
                    }
                }
            }
            return _service!!
        }

    override fun isInitialized(): Boolean = _service != null
}

/**
 * 使用方法：
 * val service : T by apiService()
 *
 * @param factory service factory用于提供retrofit对象
 */
inline fun <reified ApiService : Any> apiService(factory: ApiServiceFactory): Lazy<ApiService> {
    return ApiServiceLazy(ApiService::class, factory)
}
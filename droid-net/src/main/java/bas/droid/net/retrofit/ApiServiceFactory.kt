package bas.droid.net.retrofit

import kotlin.reflect.KClass

interface ApiServiceFactory {

    val baseUrl: String

    fun <T : Any> createService(clz: KClass<T>): T
}
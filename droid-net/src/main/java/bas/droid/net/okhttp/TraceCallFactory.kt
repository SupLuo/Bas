//package bas.droid.net.okhttp
//
//import okhttp3.Call
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import java.util.concurrent.atomic.AtomicLong
//
//interface TraceIDFactory {
//    fun generateId(): Long
//}
//
///**
// * 自增ID生成器
// */
//class IncrementIDFactory : TraceIDFactory {
//    private val callId = AtomicLong(1L) // 唯一标识一个请求
//
//    override fun generateId(): Long {
//        return callId.getAndIncrement()
//    }
//}
//
//class TraceCallFactory private constructor(
//    private val chainCallFactory: Call.Factory,
//    private val idFactory: TraceIDFactory,
//) :
//    Call.Factory {
//
//    override fun newCall(request: Request): Call {
//        return chainCallFactory.newCall(
//            request.newBuilder().tag(Trace::class.java, Trace(idFactory.generateId())).build()
//        )
//    }
//
//    companion object {
//
//        @JvmOverloads
//        fun create(
//            okHttpClient: OkHttpClient,
//            idFactory: TraceIDFactory = IncrementIDFactory()
//        ): TraceCallFactory {
//            return TraceCallFactory(okHttpClient, idFactory)
//        }
//    }
//}
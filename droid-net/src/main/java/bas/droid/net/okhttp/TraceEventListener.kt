//package bas.droid.net.okhttp
//
//import kotlin.jvm.JvmOverloads
//import okhttp3.*
//import java.io.IOException
//import java.net.InetAddress
//import java.net.InetSocketAddress
//import java.net.Proxy
//import java.util.concurrent.TimeUnit
//
///**
// * 网络请求事件追踪；通过[OkHttpClient.eventListenerFactory]进行设置
// * An OkHttp EventListener, which logs call events. Can be applied as an
// * [event listener factory][OkHttpClient.eventListenerFactory].
// *
// *
// * The format of the logs created by this class should not be considered stable and may change
// * slightly between releases. If you need a stable logging format, use your own event listener.
// */
//class TraceEventListener private constructor(id:Long) :
//    EventListener() {
//    private val trace = Trace(id)
//
//    private val logger: HttpLoggingInterceptor.Logger
//    private var startNs: Long = 0
//
//    override fun callStart(call: Call) {
//        trace.onCallStart(call)
////        startNs = System.nanoTime()
////        logWithTime("callStart: " + call.request())
//    }
//
//    override fun dnsStart(call: Call, domainName: String) {
//        trace.dnsStartNanoTime = System.nanoTime()
//        logWithTime("dnsStart: $domainName")
//    }
//
//    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
//        trace.dnsEndNanoTime = System.nanoTime()
//        logWithTime("dnsEnd: $inetAddressList")
//    }
//
//    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
//        trace.connectStartNanoTime = System.nanoTime()
//        logWithTime("connectStart: $inetSocketAddress $proxy")
//    }
//
//    override fun secureConnectStart(call: Call) {
//        trace.secureConnectStartNanoTime = System.nanoTime()
//        logWithTime("secureConnectStart")
//    }
//
//    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
//        trace.secureConnectEndNanoTime = System.nanoTime()
//        logWithTime("secureConnectEnd")
//    }
//
//    override fun connectEnd(
//        call: Call,
//        inetSocketAddress: InetSocketAddress,
//        proxy: Proxy,  protocol: Protocol?
//    ) {
//        trace.connectEndNanoTime = System.nanoTime()
//        logWithTime("connectEnd: $protocol")
//    }
//
//    override fun connectFailed(
//        call: Call,
//        inetSocketAddress: InetSocketAddress,
//        proxy: Proxy, protocol: Protocol?,
//        ioe: IOException
//    ) {
//        trace.connectFailedNanoTime = System.nanoTime()
//        logWithTime("connectFailed: $protocol $ioe")
//    }
//
//    override fun connectionAcquired(call: Call, connection: Connection) {
//        logWithTime("connectionAcquired: $connection")
//    }
//
//    override fun connectionReleased(call: Call, connection: Connection) {
//        trace.connectionReleasedNanoTime = System.nanoTime()
//        logWithTime("connectionReleased")
//    }
//
//    override fun requestHeadersStart(call: Call) {
//        logWithTime("requestHeadersStart")
//    }
//
//    override fun requestHeadersEnd(call: Call, request: Request) {
//        logWithTime("requestHeadersEnd")
//    }
//
//    override fun requestBodyStart(call: Call) {
//        logWithTime("requestBodyStart")
//    }
//
//    override fun requestBodyEnd(call: Call, byteCount: Long) {
//        logWithTime("requestBodyEnd: byteCount=$byteCount")
//    }
//
//    override fun responseHeadersStart(call: Call) {
//        logWithTime("responseHeadersStart")
//    }
//
//    override fun responseHeadersEnd(call: Call, response: Response) {
//        logWithTime("responseHeadersEnd: $response")
//    }
//
//    override fun responseBodyStart(call: Call) {
//        logWithTime("responseBodyStart")
//    }
//
//    override fun responseBodyEnd(call: Call, byteCount: Long) {
//        logWithTime("responseBodyEnd: byteCount=$byteCount")
//    }
//
//    override fun callEnd(call: Call) {
//        logWithTime("callEnd")
//    }
//
//    override fun callFailed(call: Call, ioe: IOException) {
//        logWithTime("callFailed: $ioe")
//    }
//
//    private fun logWithTime(message: String) {
//        val timeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
//        logger.log("[$timeMs ms] $message")
//    }
//
//    class Factory @JvmOverloads constructor(logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT) :
//        EventListener.Factory {
//        private val logger: HttpLoggingInterceptor.Logger
//        override fun create(call: Call): EventListener {
//            return TraceEventListener(logger)
//        }
//
//        init {
//            this.logger = logger
//        }
//    }
//
//    init {
//        this.logger = logger
//    }
//}
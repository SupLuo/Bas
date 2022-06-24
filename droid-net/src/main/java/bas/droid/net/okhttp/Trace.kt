package bas.droid.net.okhttp

import okhttp3.*
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * [callStartNanoTime] 请求开始的时间
 * 其他时间均为执行到该步骤时消耗的时间
 */
class Trace(val id: Long) {

    var url: String = ""
        internal set

    /**
     * 请求开始时间（纳秒）
     */
    var callStartNanoTime: Long = 0
        internal set

    /**
     * 请求结束消耗的时间（纳秒）
     */
    var callEndNanoTime: Long = 0

    /**
     * DNS解析开始（纳秒）
     */
    var dnsStartNanoTime: Long = 0
        internal set

    /**
     * DNS解析结束（纳秒）
     */
    var dnsEndNanoTime: Long = 0
        internal set

    /**
     * 开始连接服务器（纳秒）
     */
    var connectStartNanoTime: Long = 0
        internal set

    /**
     * 开始安全连接（纳秒）
     */
    var secureConnectStartNanoTime: Long = 0
        internal set

    /**
     * 结束安全连接（纳秒）
     */
    var secureConnectEndNanoTime: Long = 0
        internal set

    /**
     * 连接结束(可能连接成功，也可能连接失败)
     */
    var connectEndNanoTime: Long = 0
        internal set

    /**
     * 释放连接
     */
    var connectionReleasedNanoTime: Long = 0
        internal set

    fun onCallStart(call: Call) {
        callStartNanoTime = System.nanoTime()
        url = call.request().url().toString()
    }

    fun onDnsStart(call: Call, domainName: String) {
        dnsStartNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onDnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        dnsEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onConnectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        connectStartNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onSecureConnectStart(call: Call) {
        secureConnectStartNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onSecureConnectEnd(call: Call, handshake: Handshake?) {
        secureConnectEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onConnectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy, protocol: Protocol?
    ) {
        connectEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onConnectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy, protocol: Protocol?,
        ioe: IOException
    ) {
        connectEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onConnectionAcquired(call: Call, connection: Connection) {
    }

    fun onConnectionReleased(call: Call, connection: Connection) {
        connectionReleasedNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onRequestHeadersStart(call: Call) {
    }

    fun onRequestHeadersEnd(call: Call, request: Request) {
    }

    fun onRequestBodyStart(call: Call) {
    }

    fun onRequestBodyEnd(call: Call, byteCount: Long) {
    }

    fun onResponseHeadersStart(call: Call) {
    }

    fun onResponseHeadersEnd(call: Call, response: Response) {
    }

    fun onResponseBodyStart(call: Call) {
    }

    fun onResponseBodyEnd(call: Call, byteCount: Long) {
    }

    fun onCallEnd(call: Call) {
        callEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    fun onCallFailed(call: Call, ioe: IOException) {
        callEndNanoTime = System.nanoTime() - callStartNanoTime
    }

    /**
     * 消耗的总时间
     */
    fun callTimeMillisecond(): Long =
        TimeUnit.NANOSECONDS.toMillis(callEndNanoTime - callStartNanoTime)
}
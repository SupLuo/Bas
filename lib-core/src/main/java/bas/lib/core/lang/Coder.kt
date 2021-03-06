package bas.lib.core.lang

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Lucio on 2022/2/23.
 *
 */
object Coder {

    interface Base64Encoder {

        fun encode(input: ByteArray): ByteArray

        fun encodeToString(input: ByteArray): String
    }

    interface Base64Decoder {

        fun decode(input: ByteArray): ByteArray

        fun decodeToString(input: ByteArray): String

    }

    interface URLCoder {

        fun encode(value: String): String

        fun decode(value: String): String

    }

    private var base64Decoder: Base64Decoder = Base64DecoderJava()

    private var base64Encoder: Base64Encoder = Base64EncoderJava()

    private var urlCoder: URLCoder = URLCoderJava

    @JvmStatic
    fun getBase64Encoder(): Base64Encoder {
        return base64Encoder
    }

    @JvmStatic
    fun getBase64Decoder(): Base64Decoder {
        return base64Decoder
    }

    @JvmStatic
    fun setBase64Encoder(encoder: Base64Encoder) {
        base64Encoder = encoder
    }

    @JvmStatic
    fun setBase64Decoder(decoder: Base64Decoder) {
        base64Decoder = decoder
    }

    @JvmStatic
    fun getURLCoder(): URLCoder {
        return urlCoder
    }

    @JvmStatic
    fun setURLCoder(coder: URLCoder) {
        this.urlCoder = coder
    }

    @JvmStatic
    fun urlEncode(input: String): String {
        return urlCoder.encode(input)
    }

    @JvmStatic
    fun urlDecode(input: String): String {
        return urlCoder.decode(input)
    }

    @JvmStatic
    fun base64Encode(input: ByteArray): ByteArray {
        return base64Encoder.encode(input)
    }

    @JvmStatic
    fun base64Encode(input: String, charset: Charset = Charsets.UTF_8): ByteArray =
        base64Encode(input.toByteArray(charset))

    @JvmStatic
    fun base64EncodeToString(input: ByteArray): String {
        return base64Encoder.encodeToString(input)
    }

    @JvmStatic
    fun base64EncodeToString(input: String, charset: Charset = Charsets.UTF_8): String =
        base64EncodeToString(input.toByteArray(charset))

    @JvmStatic
    fun base64Decode(input: ByteArray): ByteArray {
        return base64Decoder.decode(input)
    }

    @JvmStatic
    fun base64Decode(input: String, charset: Charset = Charsets.UTF_8): ByteArray =
        base64Decode(input.toByteArray(charset))

    @JvmStatic
    fun base64DecodeToString(input: ByteArray): String {
        return base64Decoder.decodeToString(input)
    }

    @JvmStatic
    fun base64DecodeToString(input: String, charset: Charset = Charsets.UTF_8): String =
        base64DecodeToString(input.toByteArray(charset))

    /**
     * ????????????????????????
     */
    @JvmStatic
    fun replaceAllWrap(input: String): String {
        return input.replace("[\\s*\t\n\r]".toRegex(), "")
    }

    class Base64EncoderJava(private val flag: Int = NO_WRAP) : Base64Encoder {

        companion object {
            /**
             * ???RFC 822????????????76?????????????????????????????????????????????
             */
            const val DEFAULT = 0

            /**
             * ????????????
             */
            const val NO_WRAP = 2
        }

        override fun encode(input: ByteArray): ByteArray {
            return encodeToString(input).toByteArray()
        }

        @Suppress("NewApi")
        override fun encodeToString(input: ByteArray): String {

            /**
             * ?????????????????????????????????????????????????????????
             * ???RFC 822????????????76?????????????????????????????????????????????
             * ????????????????????????????????????????????????????????????????????????????????????????????????
             *
             * ?????????https://www.cnblogs.com/wudage/p/7680261.html
             */
//            val encoded = BASE64Encoder().encode(input)
            val encoded = Base64.getEncoder().encodeToString(input)
            if (flag == NO_WRAP) {
                return replaceAllWrap(encoded)
            }
            return encoded
        }
    }

    class Base64DecoderJava : Base64Decoder {

        @Suppress("NewApi")
        override fun decode(input: ByteArray): ByteArray {
            return Base64.getDecoder().decode(input)
//            return BASE64Decoder().decodeBuffer(String(input))
        }

        override fun decodeToString(input: ByteArray): String {
            return String(decode(input))
        }

    }

    /**
     * Created by Lucio on 2022/2/13.
     */
    object URLCoderJava : URLCoder {

        override fun encode(value: String): String {
            return URLEncoder.encode(value)
        }

        fun encodeJava(value: String, charsetName: String): String {
            return URLEncoder.encode(value, charsetName)
        }

        override fun decode(value: String): String {
            return URLDecoder.decode(value)
        }

        fun decodeJava(value: String, charsetName: String): String {
            return URLDecoder.decode(value, charsetName)
        }
    }
}


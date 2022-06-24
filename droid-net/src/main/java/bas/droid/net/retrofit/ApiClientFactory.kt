//package bas.droid.net.retrofit
//
//import bas.droid.net.okhttp.TraceCallFactory
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//
//class ApiClientFactory {
//
//    fun createRetrofit(okHttpClient: OkHttpClient,
//                       traceEnable:Boolean){
//        Retrofit.Builder()
//            .callFactory(TraceCallFactory.create(okHttpClient))
//
//    }
//
//
//
//    fun create(){
//
//    }
//
//}
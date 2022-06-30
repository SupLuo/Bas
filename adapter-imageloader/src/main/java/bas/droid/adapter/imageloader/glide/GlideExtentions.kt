/**
 * Created by Lucio on 2021/11/4.
 */
package bas.droid.adapter.imageloader.glide

import bas.droid.adapter.imageloader.DEBUG
import bas.droid.adapter.imageloader.ImageLoaderAdapter
import bas.droid.adapter.imageloader.logi
import com.bumptech.glide.load.engine.DiskCacheStrategy

//
///**
// * 默认请求参数
// */
//var defaultRequestOptions = RequestOptionsBas()
//    set(value) {
//        field = value
//        defaultCircleRequestOptions = value.clone().also {
//            it.transform(CenterCrop(), CircleCrop())
//        }
//    }
//
///**
// * 默认圆形请求参数
// */
//var defaultCircleRequestOptions = defaultRequestOptions.clone().also {
//    it.transform(CenterCrop(), CircleCrop())
//}

/**
 * 创建请求参数
 */
fun RequestOptionsAIL(): com.bumptech.glide.request.RequestOptions {
    if(DEBUG){
        logi("create RequestOptionsAIL")
    }
    return com.bumptech.glide.request.RequestOptions()
        .skipMemoryCache(ImageLoaderAdapter.config.isMemoryCacheEnabled)
        .diskCacheStrategy(if (!ImageLoaderAdapter.config.isDiskCacheEnabled) DiskCacheStrategy.NONE else DiskCacheStrategy.AUTOMATIC)
        .format(
            com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565
        )
//        .onlyRetrieveFromCache(true)
}
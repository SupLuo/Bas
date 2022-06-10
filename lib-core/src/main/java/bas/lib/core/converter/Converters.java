package bas.lib.core.converter;

import bas.lib.core.converter.fastjson.FastJsonConverter;
import bas.lib.core.converter.gson.GsonConverter;
import bas.lib.core.converter.jackson.JacksonConverter;
import bas.lib.core.lang.ClassesKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Lucio on 2021/7/21.
 */
public class Converters {

    private static JsonConverter mJsonConverter;

    private Converters() {
    }

    static {
        try {
            //初始化默认JsonConverter，优先使用Jackson(Kotlin版本)
            if (ClassesKt.isClassExists("com.fasterxml.jackson.module.kotlin.KotlinModule")) {
                System.out.println("Converters：使用JacksonKotlinConverter");
                mJsonConverter = new bas.lib.core.converter.jackson.JacksonConverter();
            } else if (ClassesKt.isClassExists("com.fasterxml.jackson.databind.ObjectMapper")) {
                System.out.println("Converters：使用JacksonConverter");
                mJsonConverter = new JacksonConverter();
            } else if (ClassesKt.isClassExists("com.google.gson.Gson")) {
                System.out.println("Converters：使用GsonConverter");
                mJsonConverter = new GsonConverter();
            } else if (ClassesKt.isClassExists("com.alibaba.fastjson.JSON")) {
                System.out.println("Converters：使用FastJsonConverter");
                mJsonConverter = new FastJsonConverter();
            } else {
                System.out.println("Converters：null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setJsonConverter(@NotNull JsonConverter converter) {
        mJsonConverter = converter;
    }

    public static JsonConverter getJsonConverter() {
        return mJsonConverter;
    }

    @Nullable
    public static <T> T toObject(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObject(json, clazz);
    }

    @Nullable
    public static <T> List<T> toObjectList(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObjectList(json, clazz);
    }

    @Nullable
    public static String toJson(@Nullable Object obj) {
        return mJsonConverter.toJson(obj);
    }

}

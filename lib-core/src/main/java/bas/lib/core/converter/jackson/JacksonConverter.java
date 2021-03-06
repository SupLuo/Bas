package bas.lib.core.converter.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bas.lib.core.converter.JsonConverter;
import bas.lib.core.lang.StringUtils;

/**
 * Created by Lucio on 2021/7/21.
 */
public class JacksonConverter implements JsonConverter {

    private final ObjectMapper objectMapper;

    public JacksonConverter() {
        this(createPreferredObjectMapper());
    }

    public JacksonConverter(@NotNull ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, Class<T> clazz) {
        if (StringUtils.isNullOrEmpty(json))
            return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new DeserializeException(e);
        }
    }

    @Nullable
    @Override
    public <T> List<T> toObjectList(@Nullable String json, Class<T> clazz) {
        if (StringUtils.isNullOrEmpty(json))
            return null;
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new DeserializeException(e);
        }
    }

    @Nullable
    @Override
    public String toJson(@Nullable Object obj) {
        if (obj == null)
            return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializeException(e);
        }
    }

    public static ObjectMapper createPreferredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //???????????????????????????????????????
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * ??????UTC????????????
     *
     * @param om
     */
    public static ObjectMapper applyUTCDateFormat(ObjectMapper om) {
        //Jackson?????????????????????UTC???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //Jackson?????????????????????UTC????????????,??????????????????????????????dateformat????????????format??????????????????????????????
//        om.setDateFormat(DateUtils.getUTCDateTimeFormat());
        return om;
    }
}

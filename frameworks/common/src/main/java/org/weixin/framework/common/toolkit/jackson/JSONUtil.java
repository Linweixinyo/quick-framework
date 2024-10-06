package org.weixin.framework.common.toolkit.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public final class JSONUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.registerModules(new JacksonModule());
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
    }


    public static String toJsonStr(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("object to json error: {}", e.getMessage(), e);
            return StrUtil.EMPTY;
        }
    }

    public static <T> T parseObject(String jsonStr, Class<T> clazz) {
        if(StrUtil.isNotBlank(jsonStr)) {
            try {
                return OBJECT_MAPPER.readValue(jsonStr, clazz);
            } catch (JsonProcessingException e) {
                log.error("json to object error: {}", e.getMessage(), e);
            }
        }
        return null;
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(text, typeReference);
        } catch (JsonProcessingException e) {
            log.error("json to object error: {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> List<T> parseArray(String jsonStr, Class<T> clazz) {
        if (StrUtil.isNotBlank(jsonStr)) {
            try {
                return OBJECT_MAPPER.readValue(jsonStr, OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (JsonProcessingException e) {
                log.error("json to object array error: {}", e.getMessage(), e);
            }
        }
       return Collections.emptyList();
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}

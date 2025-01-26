package org.weixin.framework.common.toolkit.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 */
@AllArgsConstructor
public class DesensitizeSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private DesensitizeType desensitizeType;

    private Integer startIndex;

    private Integer endIndex;

    private String maskStr;

    @Override
    public void serialize(String targetData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        switch (desensitizeType) {
            case EMAIL -> jsonGenerator.writeString(DesensitizedUtil.email(targetData));
            case MOBILE_PHONE -> jsonGenerator.writeString(DesensitizedUtil.mobilePhone(targetData));
            case FIXED_PHONE -> jsonGenerator.writeString(DesensitizedUtil.fixedPhone(targetData));
            case ADDRESS -> jsonGenerator.writeString(DesensitizedUtil.address(targetData, targetData.length()));
            case ID_CARD -> jsonGenerator.writeString(DesensitizedUtil.idCardNum(targetData, 2, 4));
            case CUSTOM -> jsonGenerator.writeString(StrUtil.replace(targetData, startIndex, targetData.length() - endIndex, maskStr));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 当前处理的属性为String类型
        if (Objects.nonNull(beanProperty)) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                Desensitize desensitize = beanProperty.getAnnotation(Desensitize.class);
                if (Objects.isNull(desensitize)) {
                    desensitize = beanProperty.getContextAnnotation(Desensitize.class);
                }
                if (Objects.nonNull(desensitize)) {
                    return new DesensitizeSerializer(desensitize.type(), desensitize.startIndex(),
                            desensitize.endIndex(), desensitize.maskStr());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }
}

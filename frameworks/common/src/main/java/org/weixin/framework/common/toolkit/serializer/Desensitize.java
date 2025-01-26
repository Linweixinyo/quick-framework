package org.weixin.framework.common.toolkit.serializer;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@JacksonAnnotationsInside // 标记为Jackson的组合注解
@JsonSerialize(using = DesensitizeSerializer.class)
public @interface Desensitize {

    /**
     * 脱敏数据类型
     */
    DesensitizeType type() default DesensitizeType.CUSTOM;

    /**
     * 脱敏起始位置
     */
    int startIndex() default 0;

    /**
     * 脱敏结束位置
     */
    int endIndex() default 0;

    /**
     * 打码字符串
     */
    String maskStr() default "*";
}

package org.weixin.framework.idempotent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 间隔时间
     */
    long interval() default 5000;

    /**
     * 间隔单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * SpEL表达式
     */
    String key() default "";

    /**
     * 提示消息
     */
    String message() default "您操作太快，请稍后再试";

}

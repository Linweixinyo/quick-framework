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

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String message() default "您操作太快，请稍后再试";

}

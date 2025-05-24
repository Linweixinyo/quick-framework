package org.weixin.framework.web.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidator.class})
public @interface ValidEnum {

    String message() default "Invalid value";

    Class<? extends Enum<?>> enumClass();

    String enumMethod() default "getCode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

package org.weixin.framework.web.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class EnumValidator implements ConstraintValidator<ValidEnum, Object> {

    private Class<? extends Enum<?>> enumClass;

    private String enumMethod;

    @Override
    public void initialize(ValidEnum validEnum) {
        this.enumClass = validEnum.enumClass();
        this.enumMethod = validEnum.enumMethod();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(value) || Objects.isNull(enumClass) || Objects.isNull(enumMethod)) {
            return true;
        }
        Enum<?>[] enumItems = enumClass.getEnumConstants();
        if(Objects.isNull(enumItems) || enumItems.length == 0) {
            return true;
        }
        try {
            Method method = enumClass.getMethod(enumMethod);
            for (Enum<?> enumItem : enumItems) {
                Object result = method.invoke(enumItem);
                if(Objects.equals(value, result)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("enum validate occur error：enumClass {}", enumClass.getName(), e);
        }
        return true;
    }
}

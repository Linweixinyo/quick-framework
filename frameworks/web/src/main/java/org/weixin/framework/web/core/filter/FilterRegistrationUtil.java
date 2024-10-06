package org.weixin.framework.web.core.filter;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

public class FilterRegistrationUtil {


    public static <T extends Filter> FilterRegistrationBean<T> registerFilter(T filter, int order) {
        FilterRegistrationBean<T> filterRegistration = new FilterRegistrationBean<>(filter);
        filterRegistration.setOrder(order);
        return filterRegistration;
    }

}

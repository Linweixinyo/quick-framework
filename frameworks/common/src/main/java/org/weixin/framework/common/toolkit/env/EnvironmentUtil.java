package org.weixin.framework.common.toolkit.env;

import org.springframework.core.env.Environment;
import org.weixin.framework.common.toolkit.context.ApplicationContextHolder;

import java.util.Arrays;

public class EnvironmentUtil {

    public static boolean isProdProfile() {
        Environment environment = getEnvironment();
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(activeProfile -> activeProfile.contains("prod"));
    }

    public static boolean isDevProfile() {
        Environment environment = getEnvironment();
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(activeProfile -> activeProfile.contains("dev"));
    }

    public static Environment getEnvironment() {
        return ApplicationContextHolder.getInstance().getEnvironment();
    }

    public static String getProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    public static <T> T getProperty(String key, Class<T> clazz) {
        return getEnvironment().getProperty(key, clazz);
    }
}
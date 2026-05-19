package org.weixin.framework.cache.core;

public class CacheOwnerContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static String get() {
        return CONTEXT.get();
    }

    public static void set(String owner) {
        CONTEXT.set(owner);
    }

    public static void remove() {
        CONTEXT.remove();
    }


}

package org.weixin.framework.idempotent.core;

public final class IdempotentContext {

    private static final ThreadLocal<String> KEY_LOCAL = new ThreadLocal<>();


    public static void setKey(String key) {
        KEY_LOCAL.set(key);
    }

    public static String getKey() {
        return KEY_LOCAL.get();
    }

    public static void removeKey() {
        KEY_LOCAL.remove();
    }


}

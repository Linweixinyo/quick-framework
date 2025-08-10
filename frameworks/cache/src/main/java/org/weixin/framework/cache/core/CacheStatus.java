package org.weixin.framework.cache.core;

import lombok.Getter;

/**
 * 缓存操作状态枚举
 */
@Getter
public enum CacheStatus {

    /**
     * 成功获取缓存
     */
    SUCCESS("SUCCESS", "成功获取缓存"),

    /**
     * 成功获取缓存但需要异步查询更新
     */
    SUCCESS_NEED_QUERY("SUCCESS_NEED_QUERY", "成功获取缓存但需要异步查询"),

    /**
     * 需要查询数据源
     */
    NEED_QUERY("NEED_QUERY", "需要查询数据源"),

    /**
     * 需要等待其他线程查询
     */
    NEED_WAIT("NEED_WAIT", "需要等待其他线程查询"),

    /**
     * 缓存失效成功
     */
    INVALID_SUCCESS("SUCCESS", "缓存失效成功"),

    /**
     * 空值缓存失效成功
     */
    INVALID_EMPTY_SUCCESS("EMPTY_VALUE_SUCCESS", "空值缓存失效成功"),

    /**
     * 缓存失效失败
     */
    INVALID_FAILED("FAILED", "缓存失效失败"),

    /**
     * 缓存设置成功
     */
    SET_SUCCESS("SET_SUCCESS", "缓存设置成功"),

    /**
     * 缓存设置失败
     */
    SET_FAILED("SET_FAILED", "缓存设置失败"),

    /**
     * 未知状态
     */
    UNKNOWN("UNKNOWN", "未知状态");

    private final String code;
    private final String description;

    CacheStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CacheStatus fromString(String statusStr) {
        if (statusStr == null) {
            return UNKNOWN;
        }

        for (CacheStatus status : values()) {
            if (status.code.equals(statusStr)) {
                return status;
            }
        }
        return UNKNOWN;
    }
} 
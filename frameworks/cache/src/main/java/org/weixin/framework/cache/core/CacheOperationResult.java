package org.weixin.framework.cache.core;

import lombok.Data;
import org.weixin.framework.common.toolkit.jackson.JSONUtil;

/**
 * 缓存操作结果实体类
 */
@Data
public class CacheOperationResult {

    /**
     * 缓存值
     */
    private String value;

    /**
     * 操作状态
     */
    private CacheStatus status;


    public CacheOperationResult(Object value, String statusStr) {
        this.value = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        this.status = CacheStatus.fromString(statusStr);
    }

    public CacheOperationResult(Object value, CacheStatus status) {
        this.value = value instanceof String ? (String) value : JSONUtil.toJsonStr(value);
        this.status = status;
    }

}
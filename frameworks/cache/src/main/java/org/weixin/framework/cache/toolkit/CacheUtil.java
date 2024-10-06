package org.weixin.framework.cache.toolkit;

import cn.hutool.core.util.StrUtil;

public final class CacheUtil {

    public static String buildKey(String... keys) {
        for (String key : keys) {
            if(StrUtil.isBlank(key)) {
                throw new IllegalArgumentException("构建缓存 key 不允许为空");
            }
        }
        return String.join(StrUtil.COLON, keys);
    }


}

package org.weixin.framework.cache.core;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.weixin.framework.cache.toolkit.CacheUtil;

import java.nio.charset.Charset;

@RequiredArgsConstructor
public class RedisKeySerializer implements InitializingBean, RedisSerializer<String> {

    private final String keyPrefix;

    private final String charsetName;

    private Charset charset;

    @Override
    public byte[] serialize(String key) throws SerializationException {
        if (StrUtil.isBlank(keyPrefix)) {
            return key.getBytes();
        }
        String builderKey = CacheUtil.buildKey(keyPrefix, key);
        return builderKey.getBytes();
    }

    @Override
    public String deserialize(byte[] bytes) throws SerializationException {
        return new String(bytes, charset);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        charset = Charset.forName(charsetName);
    }
}
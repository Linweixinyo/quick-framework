package org.weixin.framework.cache.core;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.weixin.framework.cache.toolkit.RedisUtil;
import org.weixin.framework.common.toolkit.threadpool.ThreadPoolBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomSpringCache extends AbstractValueAdaptingCache {

    private final String name;

    private final RedisUtil redisUtil;

    private final RedisSerializer<Object> redisSerializer = RedisSerializer.json();

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = ThreadPoolBuilder.builder()
            .workQueue(new SynchronousQueue<Runnable>())
            .rejected(new ThreadPoolExecutor.CallerRunsPolicy())
            .threadFactory("cache-loader", false)
            .build();


    public CustomSpringCache(boolean allowNullValues, String name, RedisUtil redisUtil) {
        super(allowNullValues);
        this.name = name;
        this.redisUtil = redisUtil;
    }


    @Override
    protected Object lookup(Object key) {
        String owner = generateOwner();
        CacheOperationResult result = redisUtil.getCache(owner, key.toString());
        // 等待锁释放
        while (Objects.equals(CacheStatus.NEED_WAIT, result.getStatus())) {
            ThreadUtil.sleep(100);
            result = redisUtil.getCache(owner, key.toString());
        }
        CacheOwnerContext.set(String.join(StrUtil.UNDERLINE, owner, result.getStatus().getCode()));
        String value = result.getValue();
        return StrUtil.isBlank(value) ? null : redisSerializer.deserialize(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ValueOperations<String, String> getNativeCache() {
        return redisUtil.getInstance().opsForValue();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        String owner = generateOwner();
        CacheOperationResult result = redisUtil.getCache(owner, key.toString());
        // 等待锁释放
        while (Objects.equals(CacheStatus.NEED_WAIT, result.getStatus())) {
            ThreadUtil.sleep(100);
            result = redisUtil.getCache(owner, key.toString());
        }
        Object value = result.getValue();
        if (Objects.equals(CacheStatus.NEED_QUERY, result.getStatus())) {
            // 同步加载缓存
            try {
                value = (T) valueLoader.call();
            } catch (Exception ex) {
                throw new Cache.ValueRetrievalException(key, valueLoader, ex);
            }
            redisUtil.setCache(owner, key.toString(), new String(redisSerializer.serialize(value), StandardCharsets.UTF_8), TimeUnit.MINUTES.toSeconds(30));
        } else if (Objects.equals(CacheStatus.SUCCESS_NEED_QUERY, result.getStatus())) {
            // 异步去加载缓存
            THREAD_POOL_EXECUTOR.execute(() -> {
                try {
                    redisUtil.setCache(owner, key.toString(), new String(redisSerializer.serialize(valueLoader.call()), StandardCharsets.UTF_8), TimeUnit.MINUTES.toSeconds(30));
                } catch (Exception ex) {
                    throw new Cache.ValueRetrievalException(key, valueLoader, ex);
                }
            });
        }
        return (T) fromStoreValue(value);
    }

    @Override
    public void put(Object key, Object value) {
        String[] ownerAndStatus = CacheOwnerContext.get().split(StrUtil.UNDERLINE);
        String owner = ownerAndStatus[0];
        redisUtil.setCache(owner, key.toString(), new String(redisSerializer.serialize(value), StandardCharsets.UTF_8), TimeUnit.MINUTES.toSeconds(30));
    }

    @Override
    public void evict(Object key) {
        redisUtil.invalidCache(key.toString());
    }

    @Override
    public void clear() {
        redisUtil.delete("spring:cache:*");
    }

    /**
     * 生成锁拥有者标识
     *
     * @return 唯一标识符
     */
    private String generateOwner() {
        return IdUtil.fastSimpleUUID();
    }
}

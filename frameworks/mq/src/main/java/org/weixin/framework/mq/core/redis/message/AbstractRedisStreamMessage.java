package org.weixin.framework.mq.core.redis.message;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRedisStreamMessage {

    private final Map<String, String> headers = new HashMap<>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public abstract String getStreamKey();

}

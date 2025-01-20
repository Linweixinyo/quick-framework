package org.weixin.framework.mq.core.redis.message;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRedisStreamMessage {

    private Map<String, String> headers = new HashMap<>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public abstract String getStreamKey();

}

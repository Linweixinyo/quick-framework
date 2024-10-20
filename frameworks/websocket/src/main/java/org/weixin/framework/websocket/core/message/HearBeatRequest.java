package org.weixin.framework.websocket.core.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 心跳请求
 */
@Data
@Accessors(chain = true)
public class HearBeatRequest implements WebSocketMessage {

    public static final String TYPE = "HEAR_BEAT_REQUEST";

    /**
     * 心跳内容：PING
     */
    private String content;

    @Override
    public String getType() {
        return TYPE;
    }
}

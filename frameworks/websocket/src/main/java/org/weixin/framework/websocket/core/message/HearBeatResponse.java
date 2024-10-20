package org.weixin.framework.websocket.core.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 心跳响应
 */
@Data
@Accessors(chain = true)
public class HearBeatResponse implements WebSocketMessage {

    public static final String TYPE = "HEAR_BEAT_RESPONSE";

    /**
     * 心跳内容：PONG
     */
    private String content;

    @Override
    public String getType() {
        return TYPE;
    }
}

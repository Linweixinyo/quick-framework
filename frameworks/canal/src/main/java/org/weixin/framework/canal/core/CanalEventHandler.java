package org.weixin.framework.canal.core;

public interface CanalEventHandler {

    void process(String content);

}

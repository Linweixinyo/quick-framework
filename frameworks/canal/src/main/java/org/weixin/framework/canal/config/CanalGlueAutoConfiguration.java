package org.weixin.framework.canal.config;

import org.springframework.context.annotation.Bean;
import org.weixin.framework.canal.core.CanalEventHandler;
import org.weixin.framework.canal.core.DefaultCanalEventHandler;
import org.weixin.framework.canal.core.processor.CanalBinlogEventContext;

public class CanalGlueAutoConfiguration {

    @Bean
    public CanalBinlogEventContext canalBinlogEventContext() {
        return new CanalBinlogEventContext();
    }

    @Bean
    public CanalEventHandler canalEventHandler(CanalBinlogEventContext canalBinlogEventContext) {
        return new DefaultCanalEventHandler(canalBinlogEventContext);
    }
}

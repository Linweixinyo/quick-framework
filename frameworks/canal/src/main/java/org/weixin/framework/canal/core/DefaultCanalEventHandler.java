package org.weixin.framework.canal.core;


import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.weixin.framework.canal.core.model.CanalBinLogEvent;
import org.weixin.framework.canal.core.model.ModelTable;
import org.weixin.framework.canal.core.processor.CanalBinlogEventContext;

@RequiredArgsConstructor
public class DefaultCanalEventHandler implements CanalEventHandler {

    private final CanalBinlogEventContext canalBinlogEventContext;

    @Override
    public void process(String content) {
        CanalBinLogEvent canalBinLogEvent = JSONUtil.toBean(content, CanalBinLogEvent.class);
        ModelTable modelTable = new ModelTable()
                .setDatabase(canalBinLogEvent.getDatabase())
                .setTable(canalBinLogEvent.getTable());
        canalBinlogEventContext.getProcessors(modelTable).forEach(processor -> processor.process(modelTable, canalBinLogEvent));
    }
}

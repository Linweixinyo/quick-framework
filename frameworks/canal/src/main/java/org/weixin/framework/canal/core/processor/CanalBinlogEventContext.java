package org.weixin.framework.canal.core.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.weixin.framework.canal.core.model.ModelTable;

import java.util.*;

public class CanalBinlogEventContext implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final Map<ModelTable, List<CanalBinlogEventProcessor>> processorsMap = new HashMap<>();

    public void register(ModelTable modelTable, CanalBinlogEventProcessor<?> canalBinlogEventProcessor) {
        processorsMap.putIfAbsent(modelTable, new LinkedList<>());
        processorsMap.get(modelTable).add(canalBinlogEventProcessor);
    }


    public List<CanalBinlogEventProcessor> getProcessors(ModelTable modelTable) {
        List<CanalBinlogEventProcessor> canalBinlogEventProcessors = processorsMap.get(modelTable);
        if (Objects.isNull(canalBinlogEventProcessors) || canalBinlogEventProcessors.isEmpty()) {
            throw new IllegalArgumentException(String.format("Processor Not Found For %s", modelTable));
        }
        return canalBinlogEventProcessors;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        applicationContext.getBeansOfType(CanalBinlogEventProcessor.class)
                .forEach((beanName, processor) -> register(processor.getModelTable(), processor));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

package org.weixin.framework.designpattern.strategy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AbstractStrategyChoose implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final Map<String, AbstractExecuteStrategy> abstractExecuteStrategyMap = new HashMap<>();

    public AbstractExecuteStrategy choose(String mark) {
        return Optional.ofNullable(abstractExecuteStrategyMap.get(mark))
                .orElseThrow(() -> new RuntimeException(String.format("[%s] 策略未定义", mark)));
    }

    public <REQUEST> void chooseAndExecute(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark);
        executeStrategy.execute(requestParam);
    }


    public <REQUEST, RESPONSE> RESPONSE chooseAndExecuteResp(String mark, REQUEST requestParam) {
        AbstractExecuteStrategy executeStrategy = choose(mark);
        return (RESPONSE) executeStrategy.executeResp(requestParam);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractExecuteStrategy> executeStrategyMap = applicationContext.getBeansOfType(AbstractExecuteStrategy.class);
        executeStrategyMap.forEach((beanName, bean) -> {
            if(abstractExecuteStrategyMap.containsKey(bean.mark())) {
                throw new RuntimeException(String.format("[%s] Duplicate execution policy", bean.mark()));
            }
            abstractExecuteStrategyMap.put(bean.mark(), bean);
        });
    }
}

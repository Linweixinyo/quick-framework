package org.weixin.framework.designpattern.chain;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AbstractChainContext<T> implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final Map<String, List<AbstractChainHandler>> abstractChainHandlerContainer = new HashMap<>();


    public void handler(String mark, T requestParam) {
        List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.get(mark);
        if (CollectionUtils.isEmpty(abstractChainHandlers)) {
            throw new RuntimeException(String.format("[%s] Chain of Responsibility ID is undefined.", mark));
        }
        abstractChainHandlers.forEach(each -> each.handler(requestParam));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractChainHandler> chainFilterMap = applicationContext.getBeansOfType(AbstractChainHandler.class);
        chainFilterMap.forEach((beanName, bean) -> {
            List<AbstractChainHandler> abstractChainHandlers = abstractChainHandlerContainer.getOrDefault(bean.mark(), new ArrayList<>());
            abstractChainHandlers.add(bean);
            List<AbstractChainHandler> chainHandlers = abstractChainHandlers.stream()
                    .sorted(Comparator.comparing(Ordered::getOrder))
                    .collect(Collectors.toList());
            abstractChainHandlerContainer.put(bean.mark(), chainHandlers);
        });
    }
}

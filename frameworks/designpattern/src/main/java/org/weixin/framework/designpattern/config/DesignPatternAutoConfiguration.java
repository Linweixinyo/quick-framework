package org.weixin.framework.designpattern.config;

import org.springframework.context.annotation.Bean;
import org.weixin.framework.designpattern.chain.AbstractChainContext;
import org.weixin.framework.designpattern.state.EventHandlerContext;
import org.weixin.framework.designpattern.strategy.AbstractStrategyChoose;

public class DesignPatternAutoConfiguration {


    @Bean
    public AbstractChainContext abstractChainContext() {
        return new AbstractChainContext();
    }

    @Bean
    public EventHandlerContext eventContext() {
        return new EventHandlerContext();
    }

    @Bean
    public AbstractStrategyChoose abstractStrategyChoose() {
        return new AbstractStrategyChoose();
    }

}

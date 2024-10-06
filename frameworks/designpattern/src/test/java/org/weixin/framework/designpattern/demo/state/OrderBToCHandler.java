package org.weixin.framework.designpattern.demo.state;

import org.weixin.framework.designpattern.state.BaseState;
import org.weixin.framework.designpattern.state.EventHandler;

public class OrderBToCHandler implements EventHandler<OrderBState, OrderBToCEvent> {

    @Override
    public void execute(OrderBState currentState, OrderBToCEvent event) {
        System.out.println("OrderBState ---> OrderCState");
    }

    @Override
    public BaseState getTargetState() {
        return new OrderCState();
    }
}

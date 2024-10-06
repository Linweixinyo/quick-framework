package org.weixin.framework.designpattern.demo.state;

import org.weixin.framework.designpattern.state.BaseState;
import org.weixin.framework.designpattern.state.EventHandler;

public class OrderAToBHandler implements EventHandler<OrderAState, OrderAToBEvent> {

    @Override
    public void execute(OrderAState currentState, OrderAToBEvent event) {
        System.out.println("OrderAState ---> OrderBState");
    }

    @Override
    public BaseState getTargetState() {
        return new OrderBState();
    }
}

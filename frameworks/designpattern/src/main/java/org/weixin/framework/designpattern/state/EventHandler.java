package org.weixin.framework.designpattern.state;

public interface EventHandler<S extends BaseState, E extends BaseEvent> {

    void execute(S currentState, E event);

    BaseState getTargetState();

}

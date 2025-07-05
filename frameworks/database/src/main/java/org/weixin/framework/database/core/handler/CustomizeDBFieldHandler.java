package org.weixin.framework.database.core.handler;

import com.baomidou.mybatisplus.core.handlers.StrictFill;

import java.util.Collections;
import java.util.List;

public interface CustomizeDBFieldHandler {

    default List<StrictFill> insertFieldFill() {
        return Collections.emptyList();
    }

    default List<StrictFill> updateFieldFill() {
        return Collections.emptyList();
    }

}
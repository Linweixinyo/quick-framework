package org.weixin.framework.canal.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationType {

    /**
     * DML
     */
    DML("dml", "DML语句"),

    /**
     * DDL
     */
    DDL("ddl", "DDL语句"),
    ;

    private final String type;
    private final String description;
}

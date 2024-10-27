package org.weixin.framework.canal.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BinLogEventType {

    QUERY("QUERY", "查询"),

    INSERT("INSERT", "新增"),

    UPDATE("UPDATE", "更新"),

    DELETE("DELETE", "删除"),

    ALTER("ALTER", "列修改操作"),

    CREATE("CREATE", "表创建"),

    UNKNOWN("UNKNOWN", "未知"),

    ;

    private final String type;
    private final String description;

    public static BinLogEventType getInstance(String type) {
        for (BinLogEventType binLogType : BinLogEventType.values()) {
            if (binLogType.getType().equals(type)) {
                return binLogType;
            }
        }
        return BinLogEventType.UNKNOWN;
    }
}
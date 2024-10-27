package org.weixin.framework.canal.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModelTable {

    /**
     * 数据库
     */
    private String database;

    /**
     * 表名称
     */
    private String table;

}

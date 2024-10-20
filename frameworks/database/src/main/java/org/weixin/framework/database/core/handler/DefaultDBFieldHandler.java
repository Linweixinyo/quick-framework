package org.weixin.framework.database.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.weixin.framework.database.core.base.BaseDO;

import java.util.Date;
import java.util.Objects;

public class DefaultDBFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO baseDO) {
            Date currentDate = new Date();
            if (Objects.isNull(baseDO.getCreateTime())) {
                this.strictInsertFill(metaObject, "createTime", Date.class, currentDate);
            }
            if (Objects.isNull(baseDO.getUpdateTime())) {
                this.strictInsertFill(metaObject, "updateTime", Date.class, currentDate);
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }
}

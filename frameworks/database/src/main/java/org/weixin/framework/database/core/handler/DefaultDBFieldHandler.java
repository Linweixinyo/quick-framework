package org.weixin.framework.database.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.StrictFill;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;
import org.weixin.framework.database.core.base.BaseDO;

import java.util.Date;
import java.util.Objects;

@RequiredArgsConstructor
public class DefaultDBFieldHandler implements MetaObjectHandler {

    private final CustomizeDBFieldHandler customizeDBFieldHandler;

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
            if (Objects.isNull(baseDO.getDelFlag())) {
                this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
            }
            if (Objects.nonNull(customizeDBFieldHandler)) {
                for (StrictFill strictFill : customizeDBFieldHandler.insertFieldFill()) {
                    this.strictInsertFill(metaObject, strictFill.getFieldName(), strictFill.getFieldType(), strictFill.getFieldVal());
                }
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        if (Objects.nonNull(customizeDBFieldHandler)) {
            for (StrictFill strictFill : customizeDBFieldHandler.updateFieldFill()) {
                this.strictUpdateFill(metaObject, strictFill.getFieldName(), strictFill.getFieldType(), strictFill.getFieldVal());
            }
        }
    }
}

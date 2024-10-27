package org.weixin.framework.database.core.type;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.weixin.framework.common.toolkit.jackson.JSONUtil;


@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.LONGVARCHAR})
public abstract class JacksonTypeReferenceHandler<T> extends AbstractJsonTypeHandler<Object> {

    /**
     * 返回要反序列化的类型对象
     */
    public abstract TypeReference<T> getTypeReference();

    /**
     * 反序列化
     */
    @Override
    protected Object parse(String json) {
        return JSONUtil.parseObject(json, this.getTypeReference());
    }

    /**
     * 序列化
     */
    @Override
    protected String toJson(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }

}

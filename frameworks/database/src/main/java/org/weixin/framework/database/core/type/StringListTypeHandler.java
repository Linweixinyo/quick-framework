package org.weixin.framework.database.core.type;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes(List.class)
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.LONGVARCHAR})
public class StringListTypeHandler extends AbstractJsonTypeHandler<List<String>> {

    /**
     * 反系列化
     */
    @Override
    protected List<String> parse(String value) {
        if (StrUtil.isNotBlank(value)) {
            return StrUtil.splitTrim(value, StrUtil.COMMA);
        }
        return null;
    }

    /**
     * 序列化
     */
    @Override
    protected String toJson(List<String> obj) {
        return CollUtil.join(obj, StrUtil.COMMA);
    }
}
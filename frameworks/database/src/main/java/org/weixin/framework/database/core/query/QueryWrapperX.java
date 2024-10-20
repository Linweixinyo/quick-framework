package org.weixin.framework.database.core.query;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.Collection;
import java.util.Objects;

public class QueryWrapperX<T> extends QueryWrapper<T> {

    public QueryWrapperX<T> likeIfPresent(String column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (QueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> inIfNotEmpty(String column, Collection<?> values) {
        if (CollectionUtil.isNotEmpty(values)) {
            return (QueryWrapperX<T>) in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> inIfNotEmpty(String column, Object... values) {
        if (values != null && values.length > 0) {
            return (QueryWrapperX<T>) in(column, values);
        }
        return this;
    }

    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        if (Objects.nonNull(val)) {
            return (QueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> gtIfPresent(String column, Object val) {
        if (Objects.nonNull(val)) {
            return (QueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> geIfPresent(String column, Object val) {
        if (Objects.nonNull(val)) {
            return (QueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> ltIfPresent(String column, Object val) {
        if (Objects.nonNull(val)) {
            return (QueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> leIfPresent(String column, Object val) {
        if (Objects.nonNull(val)) {
            return (QueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public QueryWrapperX<T> betweenIfPresent(String column, Object val1, Object val2) {
        if (Objects.nonNull(val1) && Objects.nonNull(val2)) {
            return (QueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (Objects.nonNull(val1)) {
            return (QueryWrapperX<T>) ge(column, val1);
        }
        if (Objects.nonNull(val2)) {
            return (QueryWrapperX<T>) le(column, val2);
        }
        return this;
    }

}

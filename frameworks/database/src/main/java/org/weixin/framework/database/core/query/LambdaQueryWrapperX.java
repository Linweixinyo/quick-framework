package org.weixin.framework.database.core.query;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Collection;
import java.util.Objects;

public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (StrUtil.isNotBlank(val)) {
            return (LambdaQueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfNotEmpty(SFunction<T, ?> column, Collection<?> values) {
        if (CollectionUtil.isNotEmpty(values)) {
            return (LambdaQueryWrapperX<T>) in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfNotEmpty(SFunction<T, ?> column, Object... values) {
        if (values != null && values.length > 0) {
            return (LambdaQueryWrapperX<T>) in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (Objects.nonNull(val)) {
            return (LambdaQueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (Objects.nonNull(val1) && Objects.nonNull(val2)) {
            return (LambdaQueryWrapperX<T>) super.between(column, val1, val2);
        }
        if (Objects.nonNull(val1)) {
            return (LambdaQueryWrapperX<T>) ge(column, val1);
        }
        if (Objects.nonNull(val2)) {
            return (LambdaQueryWrapperX<T>) le(column, val2);
        }
        return this;
    }


}

package org.weixin.framework.database.core.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;

import java.util.Collection;
import java.util.List;

public interface BaseMapperX<T> extends BaseMapper<T> {

    default T selectOne(String field, Object value) {
        QueryWrapper<T> queryWrapper = Wrappers.<T>query()
                .eq(field, value);
        return selectOne(queryWrapper);
    }

    default T selectOne(SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>lambdaQuery()
                .eq(field, value);
        return selectOne(queryWrapper);
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        QueryWrapper<T> queryWrapper = Wrappers.<T>query()
                .eq(field1, value1)
                .eq(field2, value2);
        return selectOne(queryWrapper);
    }

    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>lambdaQuery()
                .eq(field1, value1)
                .eq(field2, value2);
        return selectOne(queryWrapper);
    }

    default List<T> selectList() {
        return selectList(Wrappers.query());
    }

    default List<T> selectList(String field, Object value) {
        QueryWrapper<T> queryWrapper = Wrappers.<T>query()
                .eq(field, value);
        return selectList(queryWrapper);
    }

    default List<T> selectList(SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>lambdaQuery()
                .eq(field, value);
        return selectList(queryWrapper);
    }

    default Long selectCount() {
        return selectCount(Wrappers.emptyWrapper());
    }

    default Long selectCount(String field, Object value) {
        QueryWrapper<T> queryWrapper = Wrappers.<T>query()
                .eq(field, value);
        return selectCount(queryWrapper);
    }

    default Long selectCount(SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>lambdaQuery()
                .eq(field, value);
        return selectCount(queryWrapper);
    }


    default boolean insertBatch(Collection<T> entities) {
        return Db.saveBatch(entities);
    }

    default boolean insertBatch(Collection<T> entities, int batchSize) {
        return Db.saveBatch(entities, batchSize);
    }

    default boolean updateBatch(Collection<T> entities) {
        return Db.updateBatchById(entities);
    }

    default boolean updateBatch(Collection<T> entities, int batchSize) {
        return Db.updateBatchById(entities, batchSize);
    }

    default boolean insertOrUpdate(T entity) {
        return Db.saveOrUpdate(entity);
    }

    default boolean insertOrUpdateBatch(Collection<T> collection) {
        return Db.saveOrUpdateBatch(collection);
    }

    default int delete(String field, String value) {
        QueryWrapper<T> queryWrapper = Wrappers.<T>query()
                .eq(field, value);
        return delete(queryWrapper);
    }

    default int delete(SFunction<T, ?> field, Object value) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.<T>lambdaQuery()
                .eq(field, value);
        return delete(queryWrapper);
    }

}

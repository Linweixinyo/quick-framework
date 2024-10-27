package org.weixin.framework.canal.core.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import org.weixin.framework.canal.core.model.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class CanalBinlogEventProcessor<T> {


    public void process(ModelTable modelTable, CanalBinLogEvent canalBinLogEvent) {
        try {
            Class<T> dataClazz = getCanalBinlogEventParameterizedType();
            List<CanalBinLogResult<T>> resultList = parse(canalBinLogEvent, dataClazz);
            Optional.ofNullable(resultList).ifPresent(list -> {
                list.forEach(result -> {
                    // insert事件
                    BinLogEventType binLogEventType = result.getBinLogEventType();
                    OperationType operationType = result.getOperationType();
                    if (BinLogEventType.INSERT == binLogEventType && OperationType.DML == operationType) {
                        processInsertInternal(result);
                    }
                    // update事件
                    if (BinLogEventType.UPDATE == binLogEventType && OperationType.DML == operationType) {
                        processUpdateInternal(result);
                    }
                    // delete事件
                    if (BinLogEventType.DELETE == binLogEventType && OperationType.DML == operationType) {
                        processDeleteInternal(result);
                    }
                    // DDL事件
                    if (OperationType.DDL == operationType) {
                        processDDLInternal(result);
                    }
                });
            });
        } catch (Exception exception) {
            onError(canalBinLogEvent, exception);
        }

    }

    public List<CanalBinLogResult<T>> parse(CanalBinLogEvent canalBinLogEvent, Class<T> dataClazz) {
        BinLogEventType eventType = BinLogEventType.getInstance(canalBinLogEvent.getType());
        if (Objects.equals(BinLogEventType.UNKNOWN, eventType) || Objects.equals(BinLogEventType.QUERY, eventType)) {
            return Collections.emptyList();
        }
        if (canalBinLogEvent.getIsDdl()) {
            CanalBinLogResult<T> canalBinLogResult = new CanalBinLogResult<T>()
                    .setOperationType(OperationType.DDL)
                    .setBinLogEventType(eventType)
                    .setDatabaseName(canalBinLogEvent.getDatabase())
                    .setTableName(canalBinLogEvent.getTable())
                    .setSql(canalBinLogEvent.getSql());
            return Collections.singletonList(canalBinLogResult);
        }
        Optional.ofNullable(canalBinLogEvent.getPkNames()).filter(x -> x.size() == 1)
                .orElseThrow(() -> new IllegalArgumentException("DML-binlog event pkNames size error"));
        List<CanalBinLogResult<T>> entryList = new ArrayList<>();
        List<Map<String, String>> data = canalBinLogEvent.getData();
        List<Map<String, String>> old = canalBinLogEvent.getOld();
        String primaryKeyName = CollectionUtil.getFirst(canalBinLogEvent.getPkNames());
        int dataSize = Optional.ofNullable(data).map(List::size).orElse(0);
        int oldSize = Optional.ofNullable(old).map(List::size).orElse(0);
        if (dataSize > 0) {
            for (int dataIndex = 0; dataIndex < dataSize; dataIndex++) {
                CanalBinLogResult<T> entry = new CanalBinLogResult<T>()
                        .setSql(canalBinLogEvent.getSql())
                        .setOperationType(OperationType.DML)
                        .setBinLogEventType(eventType)
                        .setTableName(canalBinLogEvent.getTable())
                        .setDatabaseName(canalBinLogEvent.getDatabase());
                if (oldSize > 0 && dataIndex < oldSize) {
                    Map<String, String> oldItem = old.get(dataIndex);
                    entry.setBeforeData(BeanUtil.mapToBean(oldItem, dataClazz, true, CopyOptions.create().ignoreCase()));
                }
                Map<String, String> item = data.get(dataIndex);
                entry.setAfterData(BeanUtil.mapToBean(item, dataClazz, true, CopyOptions.create().ignoreCase()));
                entry.setPrimaryKey(item.get(primaryKeyName));
            }
        }
        return entryList;
    }

    public Class<T> getCanalBinlogEventParameterizedType() {
        Class<?> targetClass = this.getClass();
        Type type = targetClass.getGenericSuperclass();
        while (!(type instanceof ParameterizedType parameterizedType)) {
            targetClass = targetClass.getSuperclass();
            type = targetClass.getGenericSuperclass();
        }
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        // 取首个元素
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length == 1) {
            return (Class<T>) actualTypeArguments[0];
        } else {
            throw new IllegalStateException("Number of type arguments must be 1");
        }
    }


    protected abstract ModelTable getModelTable();

    /**
     * 内部处理insert事件
     *
     * @param result binlog实体
     */
    protected void processInsertInternal(CanalBinLogResult<T> result) {
    }

    /**
     * 内部处理update事件
     *
     * @param result binlog实体
     */
    protected void processUpdateInternal(CanalBinLogResult<T> result) {
    }


    /**
     * 内部处理delete事件
     *
     * @param result binlog实体
     */
    protected void processDeleteInternal(CanalBinLogResult<T> result) {
    }

    /**
     * 内部处理DDL事件
     *
     * @param result binlog实体
     */
    protected void processDDLInternal(CanalBinLogResult<T> result) {
    }

    protected void onError(CanalBinLogEvent event, Throwable throwable) {

    }
}

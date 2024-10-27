package org.weixin.framework.canal.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.weixin.framework.canal.core.model.BinLogEventType;
import org.weixin.framework.canal.core.model.CanalBinLogEvent;
import org.weixin.framework.canal.core.model.CanalBinLogResult;
import org.weixin.framework.canal.core.model.OperationType;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class CanalTest {

    public static void main(String[] args) throws IOException {

        String content = StreamUtils.copyToString(new ClassPathResource("ch0.json").getInputStream(), StandardCharsets.UTF_8);

        CanalBinLogEvent canalBinLogEvent = JSONUtil.toBean(content, CanalBinLogEvent.class);
        System.out.println(JSONUtil.toJsonStr(canalBinLogEvent));
        List<CanalBinLogResult<OrderModel>> parse = parse(canalBinLogEvent, OrderModel.class);
        System.out.println(JSONUtil.toJsonStr(parse));
    }

    @Data
    public static class OrderModel {

        private Long id;

        private String orderId;

        private LocalDateTime createTime;

        private BigDecimal amount;
    }

    public static <T> List<CanalBinLogResult<T>> parse(CanalBinLogEvent canalBinLogEvent, Class<T> dataClazz) {
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
                entryList.add(entry);
            }
        }
        return entryList;
    }

}

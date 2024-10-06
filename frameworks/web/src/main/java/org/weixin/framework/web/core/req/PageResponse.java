package org.weixin.framework.web.core.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Data
@Accessors(chain = true)
public class PageResponse<T> {

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 每页显示条数
     */
    private Long size = 10L;

    /**
     * 总数
     */
    private Long total;

    /**
     * 查询数据列表
     */
    private List<T> records = Collections.emptyList();

    public <R> PageResponse<R> convert(Function<? super T, ? extends R> mapper) {
        PageResponse<R> pageResponse = new PageResponse<>();
        pageResponse.setCurrent(this.getCurrent())
                .setSize(this.getSize())
                .setTotal(this.getTotal())
                .setRecords(this.getRecords()
                        .stream()
                        .map(mapper)
                        .collect(Collectors.toList()));
        return pageResponse;
    }
}

package org.weixin.framework.database.core.page;


import lombok.Data;

@Data
public class PageRequest {

    /**
     * 当前页
     */
    private Long current = 1L;

    /**
     * 页大小
     */
    private Long size = 10L;

}

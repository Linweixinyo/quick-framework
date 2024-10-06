package org.weixin.framework.web.core.req;


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

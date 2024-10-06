package org.weixin.framework.web.core.res;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局返回对象
 * @param <T> 返回类型
 */
@Data
@Accessors(chain = true)
public class Result<T> {

    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;


}

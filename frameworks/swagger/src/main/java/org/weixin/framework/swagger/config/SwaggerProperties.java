package org.weixin.framework.swagger.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SwaggerProperties.SWAGGER_PREFIX)
public class SwaggerProperties {

    public static final String SWAGGER_PREFIX = "framework.swagger.config";

    /**
     * 标题
     */
    private String title = "标题";

    /**
     * 项目描述
     */
    private String description = "描述";

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * 作者
     */
    private String author = "作者";

    /**
     * 邮箱
     */
    private String email = "邮箱";


}

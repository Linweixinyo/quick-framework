package org.weixin.framework.swagger.config;

import cn.hutool.core.collection.CollectionUtil;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

    private final SwaggerProperties swaggerProperties;

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .name(HttpHeaders.AUTHORIZATION)
                .in(SecurityScheme.In.HEADER)
                .bearerFormat("JWT")
                .scheme("bearer");
    }


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                .components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, createAPIKeyScheme()))
                .info(new Info().title(swaggerProperties.getTitle())
                        .version(swaggerProperties.getVersion()).description(swaggerProperties.getDescription())
                        .contact(new Contact().name(swaggerProperties.getAuthor()).email(swaggerProperties.getEmail())));
    }


    /**
     * 只保留 200 响应是为了保证前端使用Script生成的接口代码不出现冲突而导致响应结构为空
     */

    @Bean
    public GlobalOpenApiCustomizer customOpenApiCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (CollectionUtil.isEmpty(paths)) {
                return;
            }
            paths.forEach((path, pathItem) -> {
                Operation get = pathItem.getGet();
                Operation post = pathItem.getPost();
                Operation delete = pathItem.getDelete();
                Operation put = pathItem.getPut();
                onlyOkStatusCode(get, post, delete, put);
            });
        };
    }

    private void onlyOkStatusCode(Operation... operations) {
        if (operations.length == 0) {
            return;
        }
        Optional<Operation> operationOptional = Arrays.stream(operations)
                .filter(Objects::nonNull)
                .findFirst();
        if (operationOptional.isEmpty()) {
            return;
        }
        Operation operation = operationOptional.get();
        ApiResponses responses = operation.getResponses();
        // 只保留 200 响应
        if (responses != null) {
            ApiResponse apiResponse = responses.get("200");
            responses.clear();
            responses.put("200", apiResponse);
        }
    }


}

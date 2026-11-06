package com.kaoyan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
public class Knife4jConfig {

    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.kaoyan.module"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(new ApiKey("Authorization", "Authorization", "header")))
                .securityContexts(List.of(SecurityContext.builder()
                        .securityReferences(List.of(SecurityReference.builder()
                                .reference("Authorization")
                                .scopes(new AuthorizationScope[]{new AuthorizationScope("global", "全部")})
                                .build()))
                        .build()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("408 AI 错题诊断与个性化复习平台")
                .description("面向计算机考研(408)的 AI 错题诊断与个性化复习平台 - 接口文档")
                .version("1.0.0")
                .build();
    }
}

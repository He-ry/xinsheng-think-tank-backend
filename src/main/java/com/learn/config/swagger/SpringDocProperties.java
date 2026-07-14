package com.learn.config.swagger;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "springdoc.configs")
@Data
public class SpringDocProperties {
    @NotEmpty(message = "标题不能为空")
    private String title;

    @NotEmpty(message = "描述不能为空")
    private String description;

    @NotEmpty(message = "作者不能为空")
    private String author;

    @NotEmpty(message = "版本不能为空")
    private String version;

    @NotEmpty(message = "扫描的 package 不能为空")
    private String url;

    @NotEmpty(message = "扫描的 email 不能为空")
    private String email;

    @NotEmpty(message = "扫描的 license 不能为空")
    private String license;

    @NotEmpty(message = "扫描的 license-url 不能为空")
    private String licenseUrl;
}

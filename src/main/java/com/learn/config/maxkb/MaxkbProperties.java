package com.learn.config.maxkb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "maxkb")
@Data
public class MaxkbProperties {
    private String host;
    private String apiKey;
    private ApiPath apiPath = new ApiPath();

    @Data
    public static class ApiPath {
        private String upload;
        private String chat;
        private String chatId;
    }
}

package com.learn.config.minio;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
@Configuration
@ConfigurationProperties(prefix="minio")
@Data
public class MinioConfig {
    private String endpoint;
    private String bucketName;
    private String accessKey;
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().credentials(this.accessKey, this.secretKey).endpoint(this.endpoint).build();
    }
}

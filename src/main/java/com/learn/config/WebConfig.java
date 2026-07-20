package com.learn.config;

import com.learn.config.swagger.SpringDocAutoConfiguration;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig
implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    @Value(value="${file.upload-dir}")
    private String uploadDir;

    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        return SpringDocAutoConfiguration.buildGroupedOpenApi((String)"信胜智库", (String)"com.learn");
    }


    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(new String[]{"*"}).allowedMethods(new String[]{"*"}).allowedHeaders(new String[]{"*"}).allowCredentials(false);
    }


    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(this.uploadDir, new String[0]).toAbsolutePath().normalize().toUri().toString();
        log.info("uploadDir = [{}]", (Object)this.uploadDir);
        log.info("Mapping static resource: /files/** -> {}", (Object)location);
        registry.addResourceHandler(new String[]{"/files/**"}).addResourceLocations(new String[]{location});
    }
}

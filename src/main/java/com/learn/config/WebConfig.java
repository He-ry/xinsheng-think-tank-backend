package com.learn.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.learn.config.swagger.SpringDocAutoConfiguration;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
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
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        return SpringDocAutoConfiguration.buildGroupedOpenApi((String)"信胜智库", (String)"com.learn");
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class, (JsonSerializer)new LocalDateTimeSerializer(DATETIME_FORMATTER));
            module.addDeserializer(LocalDateTime.class, (JsonDeserializer)new LocalDateTimeDeserializer(DATETIME_FORMATTER));
            builder.modules(new Module[]{module});
            builder.featuresToDisable(new Object[]{SerializationFeature.WRITE_DATES_AS_TIMESTAMPS});
        };
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

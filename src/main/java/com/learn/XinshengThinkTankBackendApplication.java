package com.learn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.Data;
@EnableScheduling
@SpringBootApplication
@Data
public class XinshengThinkTankBackendApplication {
    private static final Logger log = LoggerFactory.getLogger(XinshengThinkTankBackendApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(XinshengThinkTankBackendApplication.class, (String[])args);
        ConfigurableEnvironment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String path = env.getProperty("server.servlet.context-path", "");
        log.info("文档地址：{}", (Object)("http://localhost:" + port + path + "/doc.html"));
    }
}

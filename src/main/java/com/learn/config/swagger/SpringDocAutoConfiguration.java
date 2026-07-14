package com.learn.config.swagger;

import com.learn.config.swagger.SpringDocProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import lombok.Data;
/*
 * Exception performing whole class analysis ignored.
 */
@AutoConfiguration
@ConditionalOnClass(value={OpenAPI.class})
@EnableConfigurationProperties(value={SpringDocProperties.class})
@ConditionalOnProperty(prefix="springdoc.api-docs", name={"enabled"}, havingValue="true", matchIfMissing=true)
@Data
public class SpringDocAutoConfiguration {
    @Bean
    public OpenAPI createApi(SpringDocProperties properties) {
        Map<String, SecurityScheme> securitySchemas = this.buildSecuritySchemes();
        OpenAPI openAPI = new OpenAPI().info(this.buildInfo(properties)).components(new Components().securitySchemes(securitySchemas)).addSecurityItem(new SecurityRequirement().addList("Authorization"));
        securitySchemas.keySet().forEach(key -> openAPI.addSecurityItem(new SecurityRequirement().addList(key)));
        return openAPI;
    }


    private Info buildInfo(SpringDocProperties properties) {
        return new Info().title(properties.getTitle()).description(properties.getDescription()).version(properties.getVersion()).contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail())).license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }


    private Map<String, SecurityScheme> buildSecuritySchemes() {
        HashMap<String, SecurityScheme> securitySchemes = new HashMap<String, SecurityScheme>();
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("Authorization").in(SecurityScheme.In.HEADER);
        securitySchemes.put("Authorization", securityScheme);
        return securitySchemes;
    }

    @Bean
    @Primary
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI, SecurityService securityParser, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils, Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers, Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers, Optional<JavadocProvider> javadocProvider) {
        return new OpenAPIService(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return SpringDocAutoConfiguration.buildGroupedOpenApi((String)"全部模块", (String)"com.learn");
    }

    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder().group(group).packagesToScan(new String[]{path}).build();
    }
}

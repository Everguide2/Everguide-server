package com.example.everguide.config;

import com.example.everguide.jwt.CustomLogoutFilter;
import com.example.everguide.jwt.LoginFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springdoc.core.models.GroupedOpenApi;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.util.Optional;

@Configuration
public class SwaggerConfig {

    private final ApplicationContext applicationContext;

    public SwaggerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * OpenAPI를 Bean으로 등록합니다.
     *
     * @return OpenAPI
     */

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Everguide API Documentation")
                .version("v1.0.0")
                .description("Everguide 서버 API 명세서입니다.");

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .info(info);
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("group")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(newSpringSecurityLoginEndpointCustomizer(applicationContext))
                .addOpenApiCustomizer(newSpringSecurityLogoutEndpointCustomizer(applicationContext))
                .build();
    }

    @Bean
    public OpenApiCustomizer newSpringSecurityLoginEndpointCustomizer(ApplicationContext applicationContext) {
        FilterChainProxy filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
        return openAPI -> {
            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<LoginFilter> optionalFilter =
                        filterChain.getFilters().stream()
                                .filter(LoginFilter.class::isInstance)
                                .map(LoginFilter.class::cast)
                                .findAny();
                if (optionalFilter.isPresent()) {
                    LoginFilter loginFilter = optionalFilter.get();
                    Operation operation = new Operation();
                    Schema<?> schema = new ObjectSchema()
                            .addProperties(loginFilter.getUsernameParameter(), new StringSchema())
                            .addProperties(loginFilter.getPasswordParameter(), new StringSchema());
                    RequestBody requestBody = new RequestBody().content(new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, new MediaType().schema(schema)));
                    operation.requestBody(requestBody);
                    ApiResponses apiResponses = new ApiResponses();
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), new ApiResponse().description(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
                    operation.responses(apiResponses);
                    operation.addTagsItem("login-endpoint");
                    PathItem pathItem = new PathItem().post(operation);
                    openAPI.getPaths().addPathItem("/login", pathItem);
                }
            }
        };
    }

    @Bean
    public OpenApiCustomizer newSpringSecurityLogoutEndpointCustomizer(ApplicationContext applicationContext) {
        FilterChainProxy filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
        return openAPI -> {
            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<CustomLogoutFilter> optionalFilter =
                        filterChain.getFilters().stream()
                                .filter(CustomLogoutFilter.class::isInstance)
                                .map(CustomLogoutFilter.class::cast)
                                .findAny();
                if (optionalFilter.isPresent()) {
                    CustomLogoutFilter customLogoutFilter = optionalFilter.get();
                    Operation operation = new Operation();
                    ApiResponses apiResponses = new ApiResponses();
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse().description(HttpStatus.OK.getReasonPhrase()));
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), new ApiResponse().description(HttpStatus.BAD_REQUEST.getReasonPhrase()));
                    operation.responses(apiResponses);
                    operation.addTagsItem("logout-endpoint");
                    PathItem pathItem = new PathItem().post(operation);
                    openAPI.getPaths().addPathItem("/logout", pathItem);
                }
            }
        };
    }
}

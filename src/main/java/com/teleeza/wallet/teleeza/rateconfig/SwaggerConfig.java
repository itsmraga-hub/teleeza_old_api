package com.teleeza.wallet.teleeza.rateconfig;

//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//import io.swagger.v3.oas.annotations.info.Info;
//import io.swagger.v3.oas.annotations.security.SecurityScheme;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import org.springdoc.core.models.GroupedOpenApi;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@OpenAPIDefinition(info = @Info(title = "Teleeza Wallet API", version = "v1", description = "API for authentication and other services"))
//@SecurityScheme(
//        name = "bearerAuth",
//        type = SecuritySchemeType.HTTP,
//        scheme = "bearer",
//        bearerFormat = "JWT"
//)
//public class SwaggerConfig {
//
//    @Bean
//    public GroupedOpenApi publicApi() {
//        return GroupedOpenApi.builder()
//                .group("public")
//                .pathsToMatch("/public/**")
//                .build();
//    }
//
//    @Bean
//    public GroupedOpenApi privateApi() {
//        return GroupedOpenApi.builder()
//                .group("private")
//                .pathsToMatch("/**")
//                .addOpenApiCustomizer(openApi -> openApi
//                        .info(new io.swagger.v3.oas.models.info.Info()
//                                .title("Teleeza Wallet API")
//                                .version("1.0")
//                                .version("v1")
//                                .description("API for authentication and other services")
//                                .contact(new io.swagger.v3.oas.models.info.Contact()
//                                        .email("itsmraga@gmail.com")
//                                        .name("William Raga")
//                                        .url("https://teleeza.africa")
//                                )
//                        )
//                        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
//                )
//                .build();
//    }
//}

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Teleeza Wallet API", version = "v1", description = "API for authentication and other services"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0")
                        .description("API documentation for your application")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
package com.haritara.portal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for auto-generated API documentation (Medium #15)
 * Provides interactive API docs at /swagger-ui/index.html
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configure OpenAPI specification
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HariTara Research Portal API")
                        .version("1.0.0")
                        .description("REST API for managing research studies, blog posts, applications, and inquiries")
                        .contact(new Contact()
                                .name("HariTara Engineering Team")
                                .email("engineering@haritara.com")
                                .url("https://haritara.com")
                        )
                )
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local development server")
                )
                .addServersItem(new Server()
                        .url("https://api.haritara.com")
                        .description("Production server")
                );
    }
}


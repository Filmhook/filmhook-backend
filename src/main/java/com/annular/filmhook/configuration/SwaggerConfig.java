package com.annular.filmhook.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Film-hook Mediaapps Pvt Ltd- API")
                        .version("1.0.0")
                        .description("API DOCUMENTATION")
                        .contact(new Contact()
                                .name("Dinesh & Harshitha")
                                .email("dineshfilmhook@gmail.com"))
                        .license(new License().name("Apache 2.0")));
    }
}

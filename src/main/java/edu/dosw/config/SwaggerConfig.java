package edu.dosw.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI usersOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Users Management Service")
                        .description("Microservicio para gestión de usuarios - Customers, Sellers y Admins")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ECIEXPRESS")
                                .email("manuel.guarnizo-g@mail.escuelaing.edu.co")));
    }
}
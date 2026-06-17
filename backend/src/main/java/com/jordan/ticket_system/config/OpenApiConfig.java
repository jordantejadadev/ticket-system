package com.jordan.ticket_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketSystemOpenAPI() {

        return new OpenAPI()

//                .components(new Components()
//                        .addSecuritySchemes(
//                                "bearerAuth",
//                                new SecurityScheme()
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("bearer")
//                                        .bearerFormat("JWT")
//                        ))

                .info(new Info()
                        .title("Ticket System API")
                        .description("""
                            API REST para la gestión de tickets y usuarios.

                            Funcionalidades principales:
                            - Autenticación con JWT
                            - Refresh Token
                            - Gestión de usuarios
                            - Gestión de tickets
                            
                            Características:
                            - Autenticación mediante JWT almacenado en cookies HttpOnly.
                            - Refresh Token para renovar la sesión.
                            - Gestión de usuarios.
                            - Gestión de Tickets.
                            - Estadísticas de tickets.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Jordan")
                                .email("jordan@test.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://github.com/jordantejadadev/ticket-system")));
    }
}

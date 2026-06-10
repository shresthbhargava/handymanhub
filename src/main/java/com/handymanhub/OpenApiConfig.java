package com.handymanhub;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI handymanHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HandymanHub API")
                        .description("""
                                REST API for HandymanHub — a platform connecting customers with 
                                local blue-collar workers including electricians, plumbers, 
                                carpenters, masons, and construction contractor teams.
                                
                                Key features:
                                - Search workers by skill and pincode
                                - Book individual workers or full contractor teams
                                - Booking lifecycle: PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
                                - Double-booking conflict detection
                                - Worker skill assignment with experience tracking
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shresth")
                                .url("https://github.com/shresthbhargava/handymanhub"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local development server"),
                        new Server()
                                .url("http://localhost:8081")
                                .description("Docker container")
                ));
    }
}
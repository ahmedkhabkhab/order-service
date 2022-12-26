package com.bookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    WebClient webClient(ClientProperties properties,
                        WebClient.Builder builder) {
        return builder.baseUrl(properties.catalogServiceUri().toString()).build();
    }

}

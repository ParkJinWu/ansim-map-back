package com.ansim.map.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // application.yml에 있는 tmap.api-key 값을 자동으로 가져옵니다.
    @Value("${tmap.api.app-key}")
    private String tmapApiKey;

    @Bean
    public WebClient tmapWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://apis.openapi.sk.com")
                .defaultHeader("appKey", tmapApiKey)
                .build();
    }
}

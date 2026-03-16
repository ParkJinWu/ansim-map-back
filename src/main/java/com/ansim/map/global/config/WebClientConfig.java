package com.ansim.map.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class WebClientConfig {

    @Value("${tmap.api.app-key}")
    private String tmapApiKey;

    /**
     * 1. Tmap 전용 WebClient
     */
    @Bean
    public WebClient tmapWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://apis.openapi.sk.com")
                .defaultHeader("appKey", tmapApiKey)
                .build();
    }

    /**
     * 2. 공공데이터 포털 전용 WebClient
     * (ServiceKey 인코딩 문제를 방지하기 위해 별도 설정)
     */
    @Bean
    public WebClient publicDataWebClient(WebClient.Builder builder) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://apis.data.go.kr");
        // VALUES_ONLY 모드는 URI 템플릿은 인코딩하지 않고 변수 값만 인코딩하여 인증키 깨짐을 방지합니다.
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        return builder
                .uriBuilderFactory(factory)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 확장
                .build();
    }
}
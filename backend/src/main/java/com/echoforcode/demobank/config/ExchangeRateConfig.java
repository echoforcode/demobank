package com.echoforcode.demobank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
@Configuration
@EnableScheduling
public class ExchangeRateConfig {
    @Bean
    public RestClient currencyClient() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return RestClient.builder().baseUrl("https://api.currencyapi.com")
                .requestFactory(factory).build();
    }
}
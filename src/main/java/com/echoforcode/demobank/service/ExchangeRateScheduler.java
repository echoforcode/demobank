package com.echoforcode.demobank.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "currency.refresh-enabled", havingValue = "true")//to choose whether to refresh
public class ExchangeRateScheduler {
    private static final Logger LOG =
            LoggerFactory.getLogger(ExchangeRateScheduler.class);
    private final ExchangeRateService rates;
    @Scheduled(initialDelay = 0, fixedDelay = 43200000)//12hours
    public void refresh() {
        try {
            rates.refresh();
            LOG.info("Exchange rates refreshed");
        } catch (RuntimeException ex) {
// Do not log response bodies, headers or credentials.
            LOG.warn("Rate refresh failed ({}); retaining previous snapshot",
                    ex.getClass().getSimpleName());
        }
    }
}
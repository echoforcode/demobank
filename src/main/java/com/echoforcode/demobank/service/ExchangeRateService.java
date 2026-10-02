package com.echoforcode.demobank.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Service
public class ExchangeRateService {
    private static final Set<String> CODES =
            Set.of("USD", "EUR", "GBP", "JPY", "NGN", "INR");
    private final RestClient client;
    private final String apiKey;
    private volatile RateSnapshot snapshot;
    public ExchangeRateService(RestClient currencyClient,
                               @Value("${currency.api-key:}") String apiKey) {
        this.client = currencyClient;
        this.apiKey = apiKey;
    }
    public record Rate(String code, BigDecimal value) {}
    public record Meta(@JsonProperty("last_updated_at") String updatedAt) {}
    public record ApiResponse(Meta meta, Map<String, Rate> data) {}
    public record RateSnapshot(String baseCurrency, Instant asOf,
                               Instant fetchedAt, Map<String, BigDecimal> rates) {}
    public void refresh() {
        if (apiKey.isBlank()) {
            throw new IllegalStateException("CURRENCY_API_KEY is missing");
        }
        ApiResponse body = client.get()
                .uri("/v3/latest?base_currency=USD&currencies=USD,EUR,GBP,JPY,NGN,INR")
                .header("apikey", apiKey).retrieve().body(ApiResponse.class);
        if (body == null || body.data() == null || body.meta() == null) {
            throw new IllegalStateException("Incomplete rate response");
        }
        var next = new HashMap<String, BigDecimal>();
        for (String code : CODES) {
            Rate rate = body.data().get(code);
            if (rate == null || !code.equals(rate.code()) ||
                    rate.value() == null || rate.value().signum() <= 0) {
                throw new IllegalStateException("Missing or invalid rate");
            }
            next.put(code, rate.value());
        }
        if (next.get("USD").compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalStateException("Unexpected base currency");
        }
        Instant now = Instant.now();
        Instant asOf = Instant.parse(body.meta().updatedAt());
        if (asOf.isAfter(now.plusSeconds(300)) ||
                asOf.isBefore(now.minus(Duration.ofHours(48)))) {
            throw new IllegalStateException("Rate timestamp is out of range");
        }
        snapshot = new RateSnapshot("USD", asOf, now, Map.copyOf(next));
    }
    public RateSnapshot current() {
        RateSnapshot value = snapshot;
        if (value == null || value.asOf()
                .isBefore(Instant.now().minus(Duration.ofHours(48)))) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Exchange rates unavailable or older than 48 hours");
        }
        return value;
    }
}
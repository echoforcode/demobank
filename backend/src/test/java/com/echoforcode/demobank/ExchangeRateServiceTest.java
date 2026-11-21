package com.echoforcode.demobank;

import com.echoforcode.demobank.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExchangeRateServiceTest {
    @Test
    void refresh_missingApiKeyFailsBeforeHttpRequest() {
        ExchangeRateService service = new ExchangeRateService(RestClient.create(), "");
        IllegalStateException ex = assertThrows(IllegalStateException.class, service::refresh);
        assertEquals("CURRENCY_API_KEY is missing", ex.getMessage());
    }

    @Test
    void current_withoutSnapshotReturns503() {
        ExchangeRateService service = new ExchangeRateService(RestClient.create(), "test-key");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, service::current);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void refresh_validResponsePopulatesCache() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://rates.example.invalid");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ExchangeRateService service = new ExchangeRateService(builder.build(), "test-key");
        String asOf = Instant.now().minusSeconds(60).toString();
        String json = validResponse(asOf);
        server.expect(once(), requestTo(containsString("/v3/latest?base_currency=USD")))
                .andExpect(header("apikey", "test-key"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        service.refresh();

        ExchangeRateService.RateSnapshot snapshot = service.current();
        assertEquals("USD", snapshot.baseCurrency());
        assertEquals(new BigDecimal("0.90"), snapshot.rates().get("EUR"));
        assertEquals(6, snapshot.rates().size());
        server.verify();
    }

    @Test
    void refresh_missingRateRejectsPayloadAndLeavesCacheEmpty() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://rates.example.invalid");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ExchangeRateService service = new ExchangeRateService(builder.build(), "test-key");
        String json = "{\"meta\":{\"last_updated_at\":\"" + Instant.now() + "\"},\"data\":{}}";
        server.expect(once(), requestTo(containsString("/v3/latest?base_currency=USD")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        IllegalStateException ex = assertThrows(IllegalStateException.class, service::refresh);
        assertEquals("Missing or invalid rate", ex.getMessage());
        assertThrows(ResponseStatusException.class, service::current);
        server.verify();
    }

    private String validResponse(String asOf) {
        return "{\"meta\":{\"last_updated_at\":\"" + asOf + "\"},\"data\":{" +
                "\"USD\":{\"code\":\"USD\",\"value\":1}," +
                "\"EUR\":{\"code\":\"EUR\",\"value\":0.90}," +
                "\"GBP\":{\"code\":\"GBP\",\"value\":0.80}," +
                "\"JPY\":{\"code\":\"JPY\",\"value\":150}," +
                "\"NGN\":{\"code\":\"NGN\",\"value\":1500}," +
                "\"INR\":{\"code\":\"INR\",\"value\":83}}}";
    }
}

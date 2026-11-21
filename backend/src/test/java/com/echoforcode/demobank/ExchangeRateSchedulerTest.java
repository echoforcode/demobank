package com.echoforcode.demobank;

import com.echoforcode.demobank.service.ExchangeRateScheduler;
import com.echoforcode.demobank.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeRateSchedulerTest {
    @Mock
    ExchangeRateService rates;
    @InjectMocks
    ExchangeRateScheduler scheduler;

    @Test
    void refresh_delegatesToExchangeRateService() {
        scheduler.refresh();
        verify(rates).refresh();
    }

    @Test
    void refresh_failureDoesNotCrashScheduler() {
        doThrow(new IllegalStateException("provider unavailable"))
                .when(rates).refresh();

        assertDoesNotThrow(() -> scheduler.refresh());
        verify(rates).refresh();
    }
}

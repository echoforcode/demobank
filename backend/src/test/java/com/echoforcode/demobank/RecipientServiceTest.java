package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.RecipientLookupDto;
import com.echoforcode.demobank.dto.RecipientResponse;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.RecipientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientServiceTest {
    @Mock AccountRepository accounts;
    @InjectMocks
    RecipientService service;

    @Test
    void find_matchingCurrency_returnsRecipientName() {
        long number = 1234567890L;
        User owner = User.builder().firstname("Ada").lastname("Lovelace").build();
        Account account = Account.builder().accountNumber(number)
                .code("USD").owner(owner).build();
        when(accounts.findByAccountNumber(number)).thenReturn(Optional.of(account));

        RecipientResponse result = service.find(new RecipientLookupDto(number, "usd"));

        assertEquals(number, result.accountNumber());
        assertEquals("USD", result.code());
        assertEquals("Ada Lovelace", result.displayName());
    }

    @Test
    void find_unknownAccount_returns404() {
        long number = 1234567890L;
        when(accounts.findByAccountNumber(number)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.find(new RecipientLookupDto(number, "USD")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No recipient account in that currency", ex.getReason());
    }

    @Test
    void find_wrongCurrency_returns404WithoutDisclosingOwner() {
        long number = 1234567890L;
        Account account = Account.builder().accountNumber(number).code("EUR").build();
        when(accounts.findByAccountNumber(number)).thenReturn(Optional.of(account));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.find(new RecipientLookupDto(number, "USD")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No recipient account in that currency", ex.getReason());
    }
}

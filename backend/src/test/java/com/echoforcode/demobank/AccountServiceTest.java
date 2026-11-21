package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.AccountDto;
import com.echoforcode.demobank.dto.AccountResponse;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.AccountService;
import com.echoforcode.demobank.service.helper.AccountHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock AccountRepository accounts;
    @Mock AccountHelper helper;
    @InjectMocks
    AccountService service;

    @Test
    void createAccount_returnsMappedAccount() {
        AccountDto dto = new AccountDto();
        dto.setCode("USD");
        User user = User.builder().uid("user-1").build();
        Account created = Account.builder().accountId("account-1")
                .accountNumber(1234567890L).code("USD")
                .balance(new BigDecimal("1000.00"))
                .label("United States Dollar").symbol('$').owner(user).build();
        when(helper.createAccount(dto, user)).thenReturn(created);

        AccountResponse result = service.createAccount(dto, user);

        assertEquals("account-1", result.accountId());
        assertEquals(1234567890L, result.accountNumber());
        assertEquals("USD", result.code());
        assertEquals(0, result.balance().compareTo(new BigDecimal("1000.00")));
        verify(helper).createAccount(dto, user);
    }

    @Test
    void createAccount_databaseConflict_returns409() {
        AccountDto dto = new AccountDto();
        dto.setCode("USD");
        User user = User.builder().uid("user-1").build();
        when(helper.createAccount(dto, user))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createAccount(dto, user));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Account data conflict; refresh your accounts before retrying", ex.getReason());
    }

    @Test
    void getUserAccounts_mapsRepositoryResults() {
        Account usd = Account.builder().accountId("usd-1").accountNumber(1234567890L)
                .code("USD").balance(new BigDecimal("50.00")).label("Dollar")
                .symbol('$').build();
        when(accounts.findAllByOwnerUidOrderByCodeAsc("user-1"))
                .thenReturn(List.of(usd));

        List<AccountResponse> result = service.getUserAccounts("user-1");

        assertEquals(1, result.size());
        assertEquals("usd-1", result.getFirst().accountId());
        assertEquals("USD", result.getFirst().code());
    }
}

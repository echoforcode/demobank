package com.echoforcode.demobank.controller;

import com.echoforcode.demobank.dto.AccountDto;
import com.echoforcode.demobank.dto.AccountResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.service.AccountService;
import com.echoforcode.demobank.service.TransferService;
import com.echoforcode.demobank.service.ExchangeRateService;
import com.echoforcode.demobank.dto.TransferDto;
import com.echoforcode.demobank.dto.TransferResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final TransferService transferService;
    private final ExchangeRateService exchangeRateService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountDto dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(dto, user));
    }

    @GetMapping
    public List<AccountResponse> getUserAccounts(
            @AuthenticationPrincipal User user) {
        return accountService.getUserAccounts(user.getUid());
    }
    @PostMapping("/transfer")
    public TransferResponse transfer(@Valid @RequestBody TransferDto dto,
                                     @AuthenticationPrincipal User user) {
        return transferService.transfer(dto, user);
    }
    @GetMapping("/rate")
    public ExchangeRateService.RateSnapshot rates() {
        return exchangeRateService.current();
    }
}



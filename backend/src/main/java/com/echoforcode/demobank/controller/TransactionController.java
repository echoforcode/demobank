package com.echoforcode.demobank.controller;

import com.echoforcode.demobank.dto.TransactionPage;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    @GetMapping
    public TransactionPage all(@RequestParam(defaultValue = "0") String page,
                               @AuthenticationPrincipal User user) {
        return transactionService.all(page, user);
    }
    @GetMapping("/c/{cardId}")
    public TransactionPage byCard(@PathVariable String cardId,
                                  @RequestParam(defaultValue = "0") String page,
                                  @AuthenticationPrincipal User user) {
        return transactionService.byCard(cardId, page, user);
    }
    @GetMapping("/a/{accountId}")
    public TransactionPage byAccount(@PathVariable String accountId,
                                     @RequestParam(defaultValue = "0") String page,
                                     @AuthenticationPrincipal User user) {
        return transactionService.byAccount(accountId, page, user);
    }
}

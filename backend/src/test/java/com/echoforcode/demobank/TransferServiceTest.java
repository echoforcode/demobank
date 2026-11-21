package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.TransferDto;
import com.echoforcode.demobank.dto.TransferResponse;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.Transaction;
import com.echoforcode.demobank.entity.Type;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.TransactionService;
import com.echoforcode.demobank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
private static final long SENDER_NUMBER = 1234567890L;
private static final long RECIPIENT_NUMBER = 1234567891L;
private static final String USER_ID = "user-1";



@Mock
private AccountRepository accounts;

@Mock
private TransactionService transactions;

@Mock
private User user;

@Mock
private Account sender;

@Mock
private Account recipient;

@InjectMocks
private TransferService transferService;

// Sets up the repository and account calls shared by all scenarios.
private void prepareAccounts(String currency) {
    when(user.getUid()).thenReturn(USER_ID);

    when(accounts.findNumberForOwner(currency, USER_ID))
            .thenReturn(Optional.of(SENDER_NUMBER));

    // TransferService locks accounts in ascending account-number order.
    when(accounts.findLocked(SENDER_NUMBER))
            .thenReturn(Optional.of(sender));
    when(accounts.findLocked(RECIPIENT_NUMBER))
            .thenReturn(Optional.of(recipient));

    when(sender.getAccountNumber()).thenReturn(SENDER_NUMBER);
    when(sender.getOwner()).thenReturn(user);
    when(sender.getCode()).thenReturn(currency);
    when(recipient.getCode()).thenReturn(currency);
}

@Test
void transfer_success_updatesBalancesAndSavesHistory() {
    // Arrange: $30.00 transfer + $0.30 fee, from a $100.00 balance.
    prepareAccounts("USD");
    when(sender.getBalance()).thenReturn(new BigDecimal("100.00"));
    when(recipient.getBalance()).thenReturn(new BigDecimal("50.00"));
    when(recipient.getAccountNumber()).thenReturn(RECIPIENT_NUMBER);

    TransferDto dto = new TransferDto(
            "USD", RECIPIENT_NUMBER, new BigDecimal("30.00"));

    // TransactionService is mocked, so configure the two ledger entries
    // that TransferService expects it to return.
    Transaction withdrawal = Transaction.builder().txId("tx-001").build();
    Transaction deposit = Transaction.builder().txId("tx-002").build();

    when(transactions.accountEntry(
            eq(sender), eq(new BigDecimal("30.00")),
            eq(new BigDecimal("0.30")), eq(Type.WITHDRAW),
            eq("1234567890"), eq("1234567891")))
            .thenReturn(withdrawal);

    when(transactions.accountEntry(
            eq(recipient), eq(new BigDecimal("30.00")),
            eq(BigDecimal.ZERO), eq(Type.DEPOSIT),
            eq("1234567890"), eq("1234567891")))
            .thenReturn(deposit);

    // Act
    TransferResponse result = transferService.transfer(dto, user);

    // Assert: sender pays $30.30, recipient receives $30.00.
    verify(sender).setBalance(new BigDecimal("69.70"));
    verify(recipient).setBalance(new BigDecimal("80.00"));
    assertEquals(new BigDecimal("0.30"), result.fee());
    assertEquals(new BigDecimal("30.30"), result.totalDebited());
    assertEquals("COMPLETED", result.status());
    verify(transactions).persist(java.util.List.of(withdrawal, deposit));
    assertEquals("tx-001", result.transactionId());
}

@Test
void transfer_jpyFraction_throwsBadRequest() {
    // Arrange: JPY permits whole-yen amounts only.
    prepareAccounts("JPY");
    TransferDto dto = new TransferDto(
            "JPY", RECIPIENT_NUMBER, new BigDecimal("100.50"));

    // Act
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> transferService.transfer(dto, user));

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("JPY transfers must use whole yen", exception.getReason());
    verifyNoInteractions(transactions);
    verify(sender, never()).setBalance(any(BigDecimal.class));
    verify(recipient, never()).setBalance(any(BigDecimal.class));
}

    @Test
    void transfer_insufficientFunds_throwsConflict() {
    // Arrange: $100.00 transfer requires $101.00 including the 1% fee.
    prepareAccounts("USD");
    when(sender.getBalance()).thenReturn(new BigDecimal("100.00"));
    TransferDto dto = new TransferDto(
            "USD", RECIPIENT_NUMBER, new BigDecimal("100.00"));

    // Act
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> transferService.transfer(dto, user));

    // Assert
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Insufficient funds including fee", exception.getReason());
    verifyNoInteractions(transactions);
    verify(sender, never()).setBalance(any(BigDecimal.class));
    verify(recipient, never()).setBalance(any(BigDecimal.class));
    }

}

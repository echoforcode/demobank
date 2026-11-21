package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.TransactionPage;
import com.echoforcode.demobank.entity.*;
import com.echoforcode.demobank.repository.TransactionRepository;
import com.echoforcode.demobank.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock TransactionRepository transactions;
    @InjectMocks
    TransactionService service;

    private final User user = User.builder().uid("user-1").build();

    @Test
    void all_appliesPageSizeAndDescendingSort() {
        when(transactions.findAllByOwnerUid(eq("user-1"), any(Pageable.class)))
                .thenAnswer(call -> Page.empty(call.getArgument(1)));

        TransactionPage result = service.all("2", user);

        assertEquals(2, result.page());
        assertEquals(10, result.size());
        assertTrue(result.items().isEmpty());
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(transactions).findAllByOwnerUid(eq("user-1"), captor.capture());
        Pageable p = captor.getValue();
        assertEquals(2, p.getPageNumber());
        assertEquals(10, p.getPageSize());
        assertTrue(p.getSort().getOrderFor("createdAt").isDescending());
        assertTrue(p.getSort().getOrderFor("txId").isDescending());
    }

    @Test
    void byCard_filtersByCardAndOwner() {
        when(transactions.findAllByCardCardIdAndOwnerUid(eq("card-1"), eq("user-1"), any(Pageable.class)))
                .thenAnswer(call -> Page.empty(call.getArgument(2)));

        TransactionPage result = service.byCard("card-1", "0", user);

        assertTrue(result.items().isEmpty());
        verify(transactions).findAllByCardCardIdAndOwnerUid(eq("card-1"), eq("user-1"), any(Pageable.class));
    }

    @Test
    void byAccount_filtersByAccountAndOwner() {
        when(transactions.findAllByAccountAccountIdAndOwnerUid(eq("account-1"), eq("user-1"), any(Pageable.class)))
                .thenAnswer(call -> Page.empty(call.getArgument(2)));

        TransactionPage result = service.byAccount("account-1", "0", user);

        assertTrue(result.items().isEmpty());
        verify(transactions).findAllByAccountAccountIdAndOwnerUid(eq("account-1"), eq("user-1"), any(Pageable.class));
    }

    @Test
    void all_rejectsNegativePage() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.all("-1", user));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(transactions);
    }

    @Test
    void all_rejectsNonNumericPage() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.all("abc", user));
        assertEquals("page must be a non-negative integer", ex.getReason());
        verifyNoInteractions(transactions);
    }

    @Test
    void accountEntry_buildsCompletedWithdrawal() {
        Account account = Account.builder().owner(user).build();
        Transaction result = service.accountEntry(account,
                new BigDecimal("20.00"), new BigDecimal("0.20"),
                Type.WITHDRAW, "1234567890", "1234567891");

        assertSame(account, result.getAccount());
        assertSame(user, result.getOwner());
        assertEquals(Type.WITHDRAW, result.getType());
        assertEquals(Status.COMPLETED, result.getStatus());
        assertEquals(new BigDecimal("0.20"), result.getTxFee());
        assertEquals("1234567890", result.getSender());
        assertEquals("1234567891", result.getReceiver());
    }

    @Test
    void cardEntry_buildsCompletedCredit() {
        Card card = Card.builder().owner(user).build();
        Transaction result = service.cardEntry(card, new BigDecimal("10.00"), Type.CREDIT);

        assertSame(card, result.getCard());
        assertSame(user, result.getOwner());
        assertEquals(Status.COMPLETED, result.getStatus());
        assertEquals(Type.CREDIT, result.getType());
        assertEquals(BigDecimal.ZERO, result.getTxFee());
    }

    @Test
    void persist_delegatesToRepository() {
        List<Transaction> entries = List.of(Transaction.builder().build());
        service.persist(entries);
        verify(transactions).saveAllAndFlush(entries);
    }
}

package com.echoforcode.demobank;

import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {
    @Mock private AccountRepository accountRepository;
    @InjectMocks private TransferService transferService;
    @Test
    void transfer_success_updatesBothBalances() {
// ARRANGE: prepare data that the repository should return
        Account alice = new Account();
        alice.setBalance(new BigDecimal("100.00"));
        Account bob = new Account();
        bob.setBalance(new BigDecimal("20.00"));
        when(accountRepository.findById("1L"))
                .thenReturn(Optional.of(alice));
        when(accountRepository.findById("2L"))
                .thenReturn(Optional.of(bob));
    // ACT: call the REAL service
        transferService.transfer("1L", "2L", new BigDecimal("30.00"));
    // ASSERT: check the business result
        assertThat(alice.getBalance()).isEqualByComparingTo("70.00");
        assertThat(bob.getBalance()).isEqualByComparingTo("50.00");

        assertThatThrownBy(() ->
                transferService.transfer(1L, 2L, new BigDecimal("50.00"))
        ).isInstanceOf(ResponseStatusException.class);
        assertThat(alice.getBalance()).isEqualByComparingTo("20.00");
        assertThat(bob.getBalance()).isEqualByComparingTo("20.00");

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository, times(1)).findById(1L);
// Example: prove deletion was not attempted
        verify(accountRepository, never()).delete(any());
    }

}
package com.echoforcode.demobank.service;


import com.echoforcode.demobank.dto.AccountDto;
import com.echoforcode.demobank.dto.AccountResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.service.helper.AccountHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accounts;
    private final AccountHelper helper;
    @Transactional
    public AccountResponse createAccount(AccountDto dto, User user) {
        try {
            return AccountResponse.from(helper.createAccount(dto, user));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Account data conflict; refresh your accounts before retrying");
        }
    }
    @Transactional(readOnly = true)
    public List<AccountResponse> getUserAccounts(String uid) {
        return accounts.findAllByOwnerUidOrderByCodeAsc(uid).stream()
                .map(AccountResponse::from).toList();
    }
}

package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.RecipientLookupDto;
import com.echoforcode.demobank.dto.RecipientResponse;
import com.echoforcode.demobank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;
//owner id look-up
@Service
@RequiredArgsConstructor
public class RecipientService {
    private final AccountRepository accounts;
    @Transactional(readOnly = true)
    public RecipientResponse find(RecipientLookupDto dto) {
        String code = dto.code().toUpperCase(Locale.ROOT);
        var account = accounts.findByAccountNumber(dto.accountNumber())
                .orElseThrow(RecipientService::notFound);
        if (!code.equals(account.getCode())) throw notFound();
        var owner = account.getOwner();
        String name = owner.getFirstname() + " " + owner.getLastname();
        return new RecipientResponse(account.getAccountNumber(),
                account.getCode(), name);
    }
    private static ResponseStatusException notFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No recipient account in that currency");
    }
}

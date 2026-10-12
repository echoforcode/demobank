package com.echoforcode.demobank.service.helper;


//The helper holds currency metadata and builds accounts; the service coordinates its use.
import com.echoforcode.demobank.dto.AccountDto;
import com.echoforcode.demobank.entity.Account;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.AccountRepository;
import com.echoforcode.demobank.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;
import java.util.Map;
import java.math.BigDecimal;
@Component
@RequiredArgsConstructor
public class AccountHelper {
    private final AccountRepository accounts;
    private final RandomUtil random;
    private record CurrencyInfo(String label, char symbol) {}
    private static final Map<String, CurrencyInfo> CURRENCIES = Map.of(
            "USD", new CurrencyInfo("United States Dollar", '$'),
            "EUR", new CurrencyInfo("Euro", '€'),
            "GBP", new CurrencyInfo("British Pound", '£'),
            "JPY", new CurrencyInfo("Japanese Yen", '¥'),
            "NGN", new CurrencyInfo("Nigerian Naira", '₦'),
            "INR", new CurrencyInfo("Indian Rupee", '₹')

    );
    public Account createAccount(AccountDto dto, User user) {
        String code = dto.getCode().toUpperCase(Locale.ROOT);
        var currency = CURRENCIES.get(code);
        if (currency == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported currency code");
        }
        if (accounts.existsByCodeAndOwnerUid(code, user.getUid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "You already have an account in this currency");
        }
        var account = Account.builder()
                .accountNumber(newAccountNumber())
                .balance(new BigDecimal("1000")) // Fictional demo funds, matching the video.
                .accountName(user.getFirstname() + " " + user.getLastname())
                .owner(user)
                .code(code).label(currency.label()).symbol(currency.symbol())
                .build();
        return accounts.saveAndFlush(account);
    }
    private long newAccountNumber() {
        for (int attempt = 0; attempt < 20; attempt++) {
            long number = random.generateRandom(10);
            if (!accounts.existsByAccountNumber(number)) {
                return number;
            }
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not allocate an account number; try again later");
    }
}

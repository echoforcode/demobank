package com.echoforcode.demobank.dto;

import com.echoforcode.demobank.entity.Card;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.math.BigDecimal;

public record CardResponse(String cardId, String cardHolder,
                           String lastFour, BigDecimal balance, LocalDate issuedOn,
                           LocalDateTime expiresAt) {
    public static CardResponse from(Card card) {
        return new CardResponse(card.getCardId(), card.getCardHolder(),
                String.format(Locale.ROOT, "%04d", card.getCardNumber() % 10000),//last four
                card.getBalance(), card.getIss(), card.getExp());
    }
}
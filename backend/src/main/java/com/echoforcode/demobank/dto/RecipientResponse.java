package com.echoforcode.demobank.dto;

public record RecipientResponse(
        long accountNumber, String code, String displayName
) {}
package com.echoforcode.demobank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record TransferDto(
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String code,
        @NotNull @Min(1000000000L) @Max(9999999999L)
        Long recipientAccountNumber,
        @NotNull @DecimalMin("0.01") @Digits(integer = 15, fraction = 2)
        BigDecimal amount
) {}
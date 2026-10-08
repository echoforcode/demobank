package com.echoforcode.demobank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ConvertDto(
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String fromCurrency,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String toCurrency,
        @NotNull @DecimalMin("0.01") @Digits(integer = 15, fraction = 2)
        BigDecimal amount
) {}
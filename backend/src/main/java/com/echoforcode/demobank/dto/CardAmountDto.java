package com.echoforcode.demobank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CardAmountDto(
        @NotNull @DecimalMin("0.01") @Digits(integer = 15, fraction = 2)
        BigDecimal amount
) {}
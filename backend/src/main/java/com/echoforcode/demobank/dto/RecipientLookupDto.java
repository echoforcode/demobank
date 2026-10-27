package com.echoforcode.demobank.dto;

import jakarta.validation.constraints.*;
public record RecipientLookupDto(
        @NotNull @Min(1000000000L) @Max(9999999999L)
        Long accountNumber,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String code
) {}

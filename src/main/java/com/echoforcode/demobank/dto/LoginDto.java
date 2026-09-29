package com.echoforcode.demobank.dto;

import jakarta.validation.constraints.*;
//A Java record declares its data and constructor compactly.
public record LoginDto(
        @NotBlank @Email @Size(max = 200) String username,
        @NotBlank @Size(max = 72) String password
) {}
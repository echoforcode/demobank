package com.echoforcode.demobank.dto;

public record AuthResponse(
        String token, String tokenType, UserResponse user
) {}
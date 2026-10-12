package com.echoforcode.demobank.controller;


import com.echoforcode.demobank.dto.*;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(dto));
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticateUser(
            @Valid @RequestBody LoginDto dto) {
        var result = userService.authenticateUser(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.token())
                .body(result);
    }

    // Guide supplement: a real protected route to test the JWT.
    @GetMapping("/me")
    public UserResponse currentUser(@AuthenticationPrincipal User user) {
        return UserResponse.from(user);
    }
}
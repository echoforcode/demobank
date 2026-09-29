package com.echoforcode.demobank.service;

import com.echoforcode.demobank.dto.*;
import com.echoforcode.demobank.dto.UserResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Transactional
    public UserResponse registerUser(UserDto dto) {
        checkPasswordLength(dto.getPassword());
        String username = normalize(dto.getUsername());
        if (users.findByUsernameIgnoreCase(username) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Username is already registered");
        }
        var user = User.builder()
                .firstname(dto.getFirstname()).lastname(dto.getLastname())
                .username(username).password(encoder.encode(dto.getPassword()))
                .dob(dto.getDob()).tel(dto.getTel()).gender(dto.getGender())
                .tag("io_" + username).roles(List.of("USER"))
                .build();
        try {
            return UserResponse.from(users.saveAndFlush(user));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Registration conflicts with an existing record");
        }
    }

    public AuthResponse authenticateUser(LoginDto dto) {
        checkPasswordLength(dto.password());
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalize(dto.username()), dto.password()));
        var user = (User) authentication.getPrincipal();
        return new AuthResponse(jwtService.generateToken(user.getUsername()),
                "Bearer", UserResponse.from(user));
    }
    private String normalize(String username) {
            return username.trim().toLowerCase(Locale.ROOT);
    }

    private void checkPasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at most 72 UTF-8 bytes for BCrypt");
        }
    }
}
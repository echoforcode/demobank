package com.echoforcode.demobank;

import com.echoforcode.demobank.dto.AuthResponse;
import com.echoforcode.demobank.dto.LoginDto;
import com.echoforcode.demobank.dto.UserDto;
import com.echoforcode.demobank.dto.UserResponse;
import com.echoforcode.demobank.entity.User;
import com.echoforcode.demobank.repository.UserRepository;
import com.echoforcode.demobank.service.JwtService;
import com.echoforcode.demobank.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository users;
    @Mock PasswordEncoder encoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock
    JwtService jwtService;
    @InjectMocks
    UserService service;

    private UserDto registration() {
        UserDto dto = new UserDto();
        dto.setFirstname("Ada");
        dto.setLastname("Lovelace");
        dto.setUsername("  ADA@EXAMPLE.COM  ");
        dto.setPassword("safe-password-123");
        return dto;
    }

    @Test
    void registerUser_normalizesEmailAndEncodesPassword() {
        UserDto dto = registration();
        when(encoder.encode("safe-password-123")).thenReturn("hashed-password");
        when(users.saveAndFlush(any(User.class))).thenAnswer(call -> {
            User saved = call.getArgument(0);
            saved.setUid("user-1");
            return saved;
        });

        UserResponse response = service.registerUser(dto);

        assertEquals("ada@example.com", response.username());
        assertEquals("user-1", response.uid());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).saveAndFlush(captor.capture());
        User saved = captor.getValue();
        assertEquals("hashed-password", saved.getPassword());
        assertEquals("io_ada@example.com", saved.getTag());
        assertEquals(List.of("USER"), saved.getRoles());
        verify(users).findByUsernameIgnoreCase("ada@example.com");
    }

    @Test
    void registerUser_duplicateUsernameReturns409() {
        UserDto dto = registration();
        when(users.findByUsernameIgnoreCase("ada@example.com"))
                .thenReturn(User.builder().uid("existing").build());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.registerUser(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(users, never()).saveAndFlush(any(User.class));
        verifyNoInteractions(encoder);
    }

    @Test
    void registerUser_databaseConflictReturns409() {
        UserDto dto = registration();
        when(encoder.encode("safe-password-123")).thenReturn("hashed-password");
        when(users.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.registerUser(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Registration conflicts with an existing record", ex.getReason());
    }

    @Test
    void authenticateUser_returnsTokenAndNormalizedUsername() {
        User principal = User.builder().uid("user-1").username("ada@example.com")
                .firstname("Ada").lastname("Lovelace").roles(List.of("USER")).build();
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtService.generateToken("ada@example.com")).thenReturn("jwt-token");

        AuthResponse result = service.authenticateUser(
                new LoginDto("  ADA@EXAMPLE.COM  ", "safe-password-123"));

        assertEquals("jwt-token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals("ada@example.com", result.user().username());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("ada@example.com", captor.getValue().getPrincipal());
    }

    @Test
    void registerUser_rejectsPasswordOver72Utf8Bytes() {
        UserDto dto = registration();
        dto.setPassword("é".repeat(40)); // 80 UTF-8 bytes

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.registerUser(dto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verifyNoInteractions(users, encoder);
    }
}

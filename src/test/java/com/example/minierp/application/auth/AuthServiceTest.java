package com.example.minierp.application.auth;

import com.example.minierp.domain.common.exceptions.DynamicTextException;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.user.Role;
import com.example.minierp.domain.user.User;
import com.example.minierp.domain.user.UserRepository;
import com.example.minierp.infrastructure.security.JwtService;
import com.example.minierp.interfaces.rest.auth.AuthResponse;
import com.example.minierp.interfaces.rest.auth.LoginRequest;
import com.example.minierp.interfaces.rest.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldCreateUserAndReturnToken() {
        // Arrange
        RegisterRequest req = new RegisterRequest("ali", "1234", "ADMIN");
        User savedUser = User.builder()
                .username("ali")
                .password("hashed")
                .role(Role.ADMIN)
                .build();

        when(repository.existsByUsername("ali")).thenReturn(false);
        when(encoder.encode("1234")).thenReturn("hashed");
        when(repository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");

        // Act
        AuthResponse res = authService.register(req);

        // Assert
        assertThat(res.token()).isEqualTo("jwt-token");
        assertThat(res.role()).isEqualTo("ADMIN");
        verify(repository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        when(repository.existsByUsername("ali")).thenReturn(true);
        RegisterRequest req = new RegisterRequest("ali", "123", "ADMIN");

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(DynamicTextException.class)
                .hasMessageContaining("نام کاربری تکراری است.");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        User user = User.builder()
                .username("ali")
                .password("hashed")
                .role(Role.SALES)
                .build();

        when(repository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(encoder.matches("1234", "hashed")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-login");

        LoginRequest req = new LoginRequest("ali", "1234");

        AuthResponse res = authService.login(req);

        assertThat(res.token()).isEqualTo("jwt-login");
        assertThat(res.role()).isEqualTo("SALES");
    }

    @Test
    void login_ShouldThrow_WhenPasswordInvalid() {
        User user = User.builder()
                .username("ali")
                .password("hashed")
                .build();
        when(repository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        LoginRequest req = new LoginRequest("ali", "wrong");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(DynamicTextException.class)
                .hasMessageContaining("پسورد اشتباه است!");
    }

    @Test
    void login_ShouldThrow_WhenUserNotFound() {
        when(repository.findByUsername("ali")).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest("ali", "1234");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(NotFoundException.class);
    }
}

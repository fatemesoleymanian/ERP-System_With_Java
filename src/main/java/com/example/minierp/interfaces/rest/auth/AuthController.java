package com.example.minierp.interfaces.rest.auth;

import com.example.minierp.application.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String token){
        return ResponseEntity.ok(authService.refreshToken(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token){
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String username){
        authService.forgotPassword(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword){
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }
}

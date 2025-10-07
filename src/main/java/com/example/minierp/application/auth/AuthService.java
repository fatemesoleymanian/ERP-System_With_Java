package com.example.minierp.application.auth;


import com.example.minierp.domain.common.exceptions.BusinessException;
import com.example.minierp.domain.common.exceptions.DynamicTextException;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import com.example.minierp.domain.user.User;
import com.example.minierp.domain.user.UserRepository;
import com.example.minierp.infrastructure.security.JwtService;
import com.example.minierp.interfaces.rest.auth.AuthResponse;
import com.example.minierp.interfaces.rest.auth.LoginRequest;
import com.example.minierp.interfaces.rest.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService { //bcryption and generating token

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request){
        User user = User.builder()
                .username(request.username())
                .password(encoder.encode(request.password()))
                .role(request.role())
                .build();
        repository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request){
        User user = repository.findByUsername(request.username())
                .orElseThrow(() -> new NotFoundException("کاربر"));

        if (!encoder.matches(request.password(), user.getPassword())){
            throw new DynamicTextException("پسورد اشتباه است!");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }


    public AuthResponse refreshToken(String token) {
        String username = jwtService.extractUsername(token);
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("کاربر"));
        String newToken = jwtService.generateToken(user);
        return new AuthResponse(newToken);
    }

    public void logout(String token) {
        // ساده‌ترین حالت: JWT stateless است، پس فقط در سمت کلاینت حذف شود
        // اگر می‌خوای blacklist یا DB داشته باشی، اونجا ذخیره می‌کنی
    }

    public void forgotPassword(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("کاربر"));
        // تولید توکن موقت (مثلاً JWT با عمر کوتاه)
        String resetToken = jwtService.generateResetToken(user);
        // اینجا معمولاً ایمیل زده میشه به کاربر با لینک reset
        System.out.println("Password reset token: " + resetToken);
    }

    public void resetPassword(String token, String newPassword) {
        String username = jwtService.extractUsername(token);
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new DynamicTextException("Invalid token"));

        user.setPassword(encoder.encode(newPassword));
        repository.save(user);
    }
}

package com.example.minierp.application.auth;


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
                .orElseThrow(()-> new RuntimeException("User not found"));

        if (!encoder.matches(request.password(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}

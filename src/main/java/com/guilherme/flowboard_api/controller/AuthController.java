package com.guilherme.flowboard_api.controller;

import com.guilherme.flowboard_api.dto.AuthResponse;
import com.guilherme.flowboard_api.dto.LoginRequest;
import com.guilherme.flowboard_api.dto.RegisterRequest;
import com.guilherme.flowboard_api.entity.User;
import com.guilherme.flowboard_api.repository.UserRepository;
import com.guilherme.flowboard_api.security.JwtService;
import com.guilherme.flowboard_api.security.UserDetailsImpl;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.MEMBER)
                .build();

        userRepository.save(user);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), token));
    }

    @PostMapping("/login")
    @RateLimiter(name = "login", fallbackMethod = "loginRateLimitFallback")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getName(), user.getEmail(), token));
    }

    public ResponseEntity<AuthResponse> loginRateLimitFallback(LoginRequest request, io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        return ResponseEntity.status(429).body(null);
    }
}
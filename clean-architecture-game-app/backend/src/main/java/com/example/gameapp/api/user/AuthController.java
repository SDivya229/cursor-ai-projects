package com.example.gameapp.api.user;

import com.example.gameapp.application.user.UserService;
import com.example.gameapp.domain.user.User;
import com.example.gameapp.infrastructure.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request.username(), request.password());
            return ResponseEntity.ok(Map.of("id", user.getId(), "username", user.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        boolean authenticated = userService.authenticate(request.username(), request.password());
        if (authenticated) {
            var userOpt = userService.getUserByUsername(request.username());
            if (userOpt.isPresent()) {
                String token = jwtUtil.generateToken(userOpt.get().getUsername(), userOpt.get().getRoles());
                return ResponseEntity.ok(Map.of("token", token));
            }
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
} 
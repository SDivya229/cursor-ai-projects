package com.example.gameapp.application.user;

import com.example.gameapp.domain.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserById(UUID id);
    Optional<User> getUserByUsername(String username);
    User registerUser(String username, String password);
    boolean authenticate(String username, String password);
} 
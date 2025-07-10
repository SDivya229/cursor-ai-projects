package com.example.gameapp.domain.user;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
} 
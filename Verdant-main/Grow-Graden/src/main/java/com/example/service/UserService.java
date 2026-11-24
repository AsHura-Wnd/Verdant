package com.example.service;

import com.example.dto.RegisterDto;
import com.example.model.User;

import java.util.Optional;

public interface UserService {

    User registerUser(RegisterDto dto);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);
}

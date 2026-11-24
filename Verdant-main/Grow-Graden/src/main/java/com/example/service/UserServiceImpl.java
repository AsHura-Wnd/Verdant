package com.example.service;

import com.example.dto.RegisterDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' is already taken.");
        }
        if (userRepository.existsByEmail(dto.getEmail().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email '" + dto.getEmail() + "' is already registered.");
        }

        User user = new User(
                dto.getUsername().trim(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getFullName().trim(),
                dto.getEmail().trim().toLowerCase(),
                "ROLE_USER"
        );
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return username != null && userRepository.existsByUsername(username.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return email != null && userRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}

package com.example.service;

import com.example.dto.RegisterDto;
import com.example.model.User;

import java.util.Optional;

/**
 * Service contract for community member registration and account lookup operations.
 */
public interface UserService {

    /**
     * Registers a new {@code ROLE_USER} member after validating uniqueness and encoding the password.
     *
     * @param dto validated registration payload
     * @return the persisted user entity
     */
    User registerUser(RegisterDto dto);

    /**
     * Looks up a user by username.
     *
     * @param username login handle
     * @return matching user if present
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks whether a username is already registered.
     *
     * @param username login handle
     * @return {@code true} if the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email address is already registered.
     *
     * @param email contact email address
     * @return {@code true} if the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Persists a user entity directly.
     *
     * @param user user entity to save
     * @return persisted user entity
     */
    User save(User user);
}

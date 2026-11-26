package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} authentication and registration lookups.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user account by its unique username.
     *
     * @param username login handle
     * @return matching user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user account by its unique email address.
     *
     * @param email user email address
     * @return matching user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a username is already present in the database.
     *
     * @param username login handle
     * @return {@code true} if taken
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether an email address is already registered.
     *
     * @param email email address
     * @return {@code true} if registered
     */
    boolean existsByEmail(String email);
}

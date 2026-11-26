package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object capturing validated registration fields for new community members.
 */
public class RegisterDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 60, message = "Full name must be between 2 and 60 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    /**
     * Default constructor for form binding.
     */
    public RegisterDto() {
    }

    /**
     * Constructs a populated registration DTO.
     *
     * @param username unique login handle
     * @param password raw password before BCrypt encoding
     * @param fullName display name
     * @param email contact email address
     */
    public RegisterDto(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }

    /** @return the requested username */
    public String getUsername() {
        return username;
    }

    /** @param username the requested username */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the raw submitted password */
    public String getPassword() {
        return password;
    }

    /** @param password the raw submitted password */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return the registrant's full display name */
    public String getFullName() {
        return fullName;
    }

    /** @param fullName the registrant's full display name */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** @return the registrant's email address */
    public String getEmail() {
        return email;
    }

    /** @param email the registrant's email address */
    public void setEmail(String email) {
        this.email = email;
    }
}

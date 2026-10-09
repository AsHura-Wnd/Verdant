package com.example.exception;

/**
 * Specific exception thrown when a GardenProject entity cannot be found.
 * Inherits from ResourceNotFoundException (Multi-level Exception Inheritance).
 */
public class GardenProjectNotFoundException extends ResourceNotFoundException {

    public GardenProjectNotFoundException(Long id) {
        super("Garden Project", id);
    }
}

package com.example.exception;

/**
 * Specific exception thrown when a GardeningTip entity cannot be found.
 * Inherits from ResourceNotFoundException (Multi-level Exception Inheritance).
 */
public class GardeningTipNotFoundException extends ResourceNotFoundException {

    public GardeningTipNotFoundException(Long id) {
        super("Gardening Tip", id);
    }
}

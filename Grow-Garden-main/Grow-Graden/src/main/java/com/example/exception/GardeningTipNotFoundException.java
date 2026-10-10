package com.example.exception;

/**
 * Specific exception thrown when a GardeningTip entity cannot be found.
 * Inherits from ResourceNotFoundException (Multi-level Exception Inheritance).
 */
public class GardeningTipNotFoundException extends ResourceNotFoundException {

    private static final long serialVersionUID = 1L;

    public GardeningTipNotFoundException(Long id) {
        super("Gardening Tip", id);
    }
}

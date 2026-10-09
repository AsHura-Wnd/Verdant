package com.example.exception;

/**
 * Specific exception thrown when a DiscussionQuestion entity cannot be found.
 * Inherits from ResourceNotFoundException (Multi-level Exception Inheritance).
 */
public class DiscussionQuestionNotFoundException extends ResourceNotFoundException {

    public DiscussionQuestionNotFoundException(Long id) {
        super("Discussion Question", id);
    }
}

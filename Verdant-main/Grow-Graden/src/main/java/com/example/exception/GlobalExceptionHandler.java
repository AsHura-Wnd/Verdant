package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler demonstrating Spring MVC @ControllerAdvice,
 * centralized error translation, database exception shielding, and distinct
 * handling for REST API vs HTML Browser clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    ex.getMessage(),
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        if (requestUri.startsWith("/tips")) {
            return "redirect:/tips";
        } else if (requestUri.startsWith("/projects")) {
            return "redirect:/projects";
        } else if (requestUri.startsWith("/discussions")) {
            return "redirect:/discussions";
        }

        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        String requestUri = request.getRequestURI();
        String message = ex.getMessage() != null ? ex.getMessage() : "Access Denied: You do not have permission to perform this action.";

        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Forbidden",
                    message,
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:/?forbidden=true";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                              HttpServletRequest request,
                                              RedirectAttributes redirectAttributes,
                                              Model model) {
        String requestUri = request.getRequestURI();
        String userFriendlyMsg = "Database operation conflict: A record with duplicate attributes or invalid references was detected.";

        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Conflict",
                    userFriendlyMsg,
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        redirectAttributes.addFlashAttribute("errorMessage", userFriendlyMsg);
        return "redirect:/";
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public Object handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex,
                                                HttpServletRequest request,
                                                RedirectAttributes redirectAttributes,
                                                Model model) {
        String requestUri = request.getRequestURI();
        String conflictMsg = "Concurrent update conflict: This entry was updated by another member while you were making changes. Please refresh to load the latest data.";

        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Conflict",
                    conflictMsg,
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        redirectAttributes.addFlashAttribute("errorMessage", conflictMsg);
        return "redirect:/";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleValidationExceptions(Exception ex, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException manv) {
            for (FieldError error : manv.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        } else if (ex instanceof BindException be) {
            for (FieldError error : be.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }

        if (requestUri.startsWith("/api/")) {
            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Bad Request");
            body.put("message", "Input validation failed");
            body.put("fieldErrors", errors);
            body.put("path", requestUri);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }

        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgument(IllegalArgumentException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    ex.getMessage(),
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         Model model) {
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Internal Server Error",
                    "An unexpected error occurred: " + ex.getMessage(),
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        model.addAttribute("errorMessage", "An unexpected gardening platform error occurred. Please try again.");
        return "error";
    }
}

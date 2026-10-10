package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global Exception Handler demonstrating Spring MVC @ControllerAdvice,
 * centralized error translation, and distinct handling for REST vs HTML clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        String requestUri = request.getRequestURI();

        // REST API requests return JSON ErrorResponse with HTTP 404
        if (requestUri.startsWith("/api/")) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    ex.getMessage(),
                    requestUri
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        // HTML Browser requests redirect gracefully with user-friendly alert
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

        model.addAttribute("errorMessage", "An unexpected gardening platform error occurred: " + ex.getMessage());
        return "error";
    }
}

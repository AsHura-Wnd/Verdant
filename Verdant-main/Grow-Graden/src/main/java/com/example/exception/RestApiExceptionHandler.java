package com.example.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Dedicated REST API exception handler scoped to {@link RestController} endpoints.
 * Translates domain, security, validation, and database exceptions into structured
 * JSON error payloads without leaking internal stack traces or SQL details.
 */
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestApiExceptionHandler {

    /**
     * Handles missing domain entities by returning HTTP 404 Not Found.
     *
     * @param ex the missing resource exception
     * @param request the current HTTP request
     * @return standardized 404 JSON error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
    }

    /**
     * Handles authorization failures on REST endpoints with HTTP 403 Forbidden.
     *
     * @param ex the access denied exception
     * @param request the current HTTP request
     * @return standardized 403 JSON error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        String message = ex.getMessage() != null
                ? ex.getMessage()
                : "Access Denied: You do not have permission to perform this action.";
        return buildError(HttpStatus.FORBIDDEN, "Forbidden", message, request);
    }

    /**
     * Translates database constraint collisions into HTTP 409 Conflict without exposing SQL.
     *
     * @param ex the data integrity exception
     * @param request the current HTTP request
     * @return standardized 409 JSON error response
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                      HttpServletRequest request) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conflict",
                "Database operation conflict: A record with duplicate attributes or invalid references was detected.",
                request
        );
    }

    /**
     * Translates optimistic locking version mismatches into HTTP 409 Conflict.
     *
     * @param ex the optimistic locking failure exception
     * @param request the current HTTP request
     * @return standardized 409 JSON error response
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(ObjectOptimisticLockingFailureException ex,
                                                                        HttpServletRequest request) {
        return buildError(
                HttpStatus.CONFLICT,
                "Conflict",
                "Concurrent update conflict: This entry was updated by another member. Please refresh and try again.",
                request
        );
    }

    /**
     * Handles bean validation and binding failures with field-level error messages.
     *
     * @param ex the validation or bind exception
     * @param request the current HTTP request
     * @return HTTP 400 payload containing {@code fieldErrors}
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(Exception ex,
                                                                          HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException manv) {
            for (FieldError error : manv.getBindingResult().getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
        } else if (ex instanceof BindException be) {
            for (FieldError error : be.getBindingResult().getFieldErrors()) {
                fieldErrors.put(error.getField(), error.getDefaultMessage());
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Input validation failed");
        body.put("fieldErrors", fieldErrors);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles missing query parameters, invalid path/query parameter types, malformed JSON,
     * and illegal argument errors with HTTP 400 Bad Request.
     *
     * @param ex the client request exception
     * @param request the current HTTP request
     * @return standardized 400 JSON error response
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message;
        if (ex instanceof MissingServletRequestParameterException missing) {
            message = "Required request parameter '" + missing.getParameterName() + "' is missing.";
        } else if (ex instanceof MethodArgumentTypeMismatchException mismatch) {
            message = "Invalid value for parameter '" + mismatch.getName() + "'.";
        } else if (ex instanceof HttpMessageNotReadableException) {
            message = "Malformed JSON request body.";
        } else {
            message = ex.getMessage() != null ? ex.getMessage() : "Invalid request parameter.";
        }
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    /**
     * Fallback handler for unexpected server errors in REST controllers without leaking internal details.
     *
     * @param ex the unexpected exception
     * @param request the current HTTP request
     * @return standardized 500 JSON error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred while processing the API request.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status,
                                                     String error,
                                                     String message,
                                                     HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}

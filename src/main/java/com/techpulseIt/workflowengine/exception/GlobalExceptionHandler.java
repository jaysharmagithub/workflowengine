package com.techpulseIt.workflowengine.exception;


import com.techpulseIt.workflowengine.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
/**
 * Global Exception Handler for the Finance Dashboard System.
 * Ensures all API errors follow a standardized format and are logged for auditing.
 */

public class GlobalExceptionHandler {

//    /**
//     * Handles Resources Not Found (404).
//     */
//    @ExceptionHandler({
//            UserNotFoundException.class,
//            TransactionNotFoundException.class,
//            CategoryNotFoundException.class,
//            RoleNotFoundException.class
//    })
//    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest request) {
//        log.warn("Resource not found: {}", ex.getMessage());
//        return buildErrorResponse(ex, request, HttpStatus.NOT_FOUND);
//    }


//    /**
//     * 2. Duplicate Transaction Exceptions (Operation Level)
//     */
//    @ExceptionHandler(DuplicateTransactionException.class)
//    public ResponseEntity<ErrorResponse> handleDuplicateTransaction(DuplicateTransactionException ex, HttpServletRequest request) {
//        log.warn("Duplicate request ignored: {}", ex.getMessage());
//        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
//    }

    /**
     * 3. Data Integrity Violations (Database Level)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Database integrity violation: {}", ex.getMostSpecificCause().getMessage());
        Exception safeEx = new Exception("Unable to save data. A record with these details may already exist.");
        return buildErrorResponse(safeEx, request, HttpStatus.CONFLICT);
    }

//    /**
//     * Handles Specific Email Conflicts (409).
//     * High priority: Specifically tells the user their email is taken.
//     */
//    @ExceptionHandler(DuplicateEmailException.class)
//    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex, HttpServletRequest request) {
//        log.warn("Email Conflict: {}", ex.getMessage());
//        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
//    }

    /**
     * Handles General "Already Exists" Business Logic (409).
     */
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            RoleAlreadyExistException.class,
    })
    public ResponseEntity<ErrorResponse> handleAlreadyExists(RuntimeException ex, HttpServletRequest request) {
        log.warn("Entity already exists: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.CONFLICT);
    }


    /**
     * Handles @Valid Input Failures (400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Input validation failed: {}", details);
        // We pass a new exception with the combined details string
        return buildErrorResponse(new Exception("Validation failed: " + details), request, HttpStatus.BAD_REQUEST);
    }

//    /**
//     * Handles Business Logic & Date Range Failures (400).
//     */
//    @ExceptionHandler({
//            InvalidRoleAssignmentException.class,
//            InvalidDateRangeException.class
//    })
//    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request) {
//        log.warn("Bad Request: {}", ex.getMessage());
//        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
//    }

    /**
     * Handles Security Violations (403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        return buildErrorResponse(ex, request, HttpStatus.FORBIDDEN);
    }

    /**
     * Global Catch-all (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("CRITICAL system error", ex);
        // Mask the actual exception message for security in 500 errors
        return buildErrorResponse(new Exception("An internal error occurred. Please contact support."), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Catch ANY other DB issue (System Error)
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleAllOtherDbIssues(DataAccessException ex, HttpServletRequest request) {
        log.error("Serious Database Error: ", ex);
        return buildErrorResponse(new Exception("Database service is currently unavailable"), request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Private helper now correctly used by all methods
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpServletRequest request, HttpStatus http) {
        ErrorResponse error = new ErrorResponse(
                http.value(),
                http.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, http);
    }
}


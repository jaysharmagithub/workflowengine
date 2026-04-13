package com.techpulseIt.workflowengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to register an email already present in the database.
 *
 * <p><b>Selection Detail:</b>
 * This enforces <b>Identity Uniqueness</b>. By returning a <b>409 Conflict</b>,
 * the API clearly communicates that the resource (the email) is already claimed,
 * which is the standard RESTful approach for registration collisions.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs the exception with a conflict message.
     *
     * @param message Detailed context (e.g., "Email already registered: admin@test.com").
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}


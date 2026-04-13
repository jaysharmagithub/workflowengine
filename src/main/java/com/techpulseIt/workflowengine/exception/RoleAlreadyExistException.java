package com.techpulseIt.workflowengine.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attempt is made to create a Security Role that already exists.
 *
 * <p><b>Selection Detail:</b>
 * This prevents "Authority Ambiguity" within the <b>RBAC (Role-Based Access Control)</b> system.
 * It ensures that each security tier (e.g., ROLE_ADMIN) remains unique and consistent
 * across the database and the security filter chain.</p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class RoleAlreadyExistException extends RuntimeException {

    /**
     * Constructs the exception with a specific conflict message.
     *
     * @param message Detailed context, e.g., "Role 'ROLE_ADMIN' is already defined."
     */
    public RoleAlreadyExistException(String message) {
        super(message);
    }
}

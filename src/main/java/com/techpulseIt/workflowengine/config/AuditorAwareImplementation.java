package com.techpulseIt.workflowengine.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Custom implementation of {@link AuditorAware} for JPA Auditing.
 * Bridges the gap between Spring Security and the Database to track
 * who created or modified financial records.
 *
 * <p><b>Selection Detail:</b>
 * Implements a 'Safe-Fallback' strategy. If a change occurs outside a
 * user session (like a background system task), it defaults to 'SYSTEM'
 * instead of returning null, ensuring 100% audit coverage.</p>
 */
@Slf4j
public class AuditorAwareImplementation implements AuditorAware<String> {

    /**
     * Resolves the current user's identity from the {@link SecurityContextHolder}.
     *
     * @return An {@link Optional} containing the username or 'SYSTEM' fallback.
     */
    @Override
    public @NonNull Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // Check if the request is authenticated and not anonymous
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            // Log as debug so we don't flood production logs, but keep track for troubleshooting
            log.debug("JPA Auditing: No authenticated user found. Defaulting auditor to 'SYSTEM'.");
            return Optional.of("SYSTEM");
        }

        String currentUserId = authentication.getName();
        log.trace("JPA Auditing: Record modification identified by user: {}", currentUserId);

        return Optional.of(currentUserId);
    }
}


package com.techpulseIt.workflowengine.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration for JPA Auditing features.
 * Enables the automatic population of 'createdBy' and 'lastModifiedBy'
 * fields across all persistent entities.
 *
 * <p><b>Selection Detail:</b>
 * Links the {@link AuditorAwareImplementation} to the JPA lifecycle,
 * ensuring every financial transaction is digitally signed by the
 * authenticated user or the 'SYSTEM' fallback.</p>
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    /**
     * Defines the bean used to resolve the current auditor's identity.
     *
     * @return A thread-safe implementation of {@link AuditorAware}.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImplementation();
    }
}

package com.techpulseIt.workflowengine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstract base class for all persistent entities.
 * Centralizes common auditing fields and primary key logic.
 *
 * <p><b>Features:</b>
 * <ul>
 *   <li>Automatic timestamp tracking via {@link AuditingEntityListener}.</li>
 *   <li>Standardized ID generation for all database tables.</li>
 * </ul>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SoftDelete
@Getter
@Setter
public abstract class BaseEntity {
    /**
     * Primary key for the entity. Uses auto-increment strategy.
     */
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    protected Long id;

    @CreatedBy
    @Column(updatable = false)
    protected String createdBy;

    /**
     * Timestamp indicating when the record was first created.
     */
     @CreatedDate
     @Column(updatable = false)
    protected LocalDateTime createdAt;

    /**
     * User indicating the last time the record was updated.
     */
    @LastModifiedBy
    protected String updatedBy;

    /**
     * Timestamp indicating the last time the record was updated.
     */
    @LastModifiedDate
    protected LocalDateTime updatedAt;

}

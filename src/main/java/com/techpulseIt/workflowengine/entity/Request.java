package com.techpulseIt.workflowengine.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "request_id"))
public class Request extends BaseEntity {

    @Column(nullable = false)
    private String type; // LEAVE, EXPENSE

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED


    // 🔥 Important for workflow tracking (recommended)
    @Column(name = "current_step_order")
    private Integer currentStepOrder;


}

package com.techpulseIt.workflowengine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "approval_steps",
        uniqueConstraints = @UniqueConstraint(columnNames = {"request_type", "step_order"})
)
@Getter
@Setter
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class ApprovalStep extends BaseEntity {


    @Column(name = "request_type", nullable = false)
    private String requestType; // LEAVE, EXPENSE

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder; // 1, 2, 3...

    @Column(nullable = false)
    private String role; // APPROVER, ADMIN
}
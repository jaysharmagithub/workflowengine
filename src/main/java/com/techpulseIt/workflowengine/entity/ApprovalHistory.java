package com.techpulseIt.workflowengine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "approval_history")
@AttributeOverride(name = "id", column = @Column(name = "approval_history_id"))
public class ApprovalHistory extends BaseEntity {

    // 🔗 Link to Request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false)
    private String action; // APPROVED, REJECTED

    @Column(name = "action_by", nullable = false)
    private Long actionBy;

    @Column(name = "action_at", nullable = false)
    private LocalDateTime actionAt;
}

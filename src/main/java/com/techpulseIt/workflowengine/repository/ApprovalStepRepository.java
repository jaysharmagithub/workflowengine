package com.techpulseIt.workflowengine.repository;

import com.techpulseIt.workflowengine.entity.ApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {

    List<ApprovalStep> findByRequestTypeOrderByStepOrder(String requestType);

    Optional<ApprovalStep> findByRequestTypeAndStepOrder(String requestType, Integer stepOrder);
}
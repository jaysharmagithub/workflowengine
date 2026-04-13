package com.techpulseIt.workflowengine.repository;

import com.techpulseIt.workflowengine.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByRequestIdOrderByActionAtAsc(Long requestId);
}
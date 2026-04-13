package com.techpulseIt.workflowengine.repository;

import com.techpulseIt.workflowengine.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
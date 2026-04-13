package com.techpulseIt.workflowengine.repository;

import com.techpulseIt.workflowengine.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsByName(String roleName);

    Optional<Role> findByName(String roleUser);
}

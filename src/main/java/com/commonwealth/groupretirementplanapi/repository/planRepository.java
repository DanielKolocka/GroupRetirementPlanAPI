package com.commonwealth.groupretirementplanapi.repository;

import com.commonwealth.groupretirementplanapi.entity.plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface planRepository extends JpaRepository<plan, UUID> {
    List<plan> findByEmployerId(UUID employerId);
}
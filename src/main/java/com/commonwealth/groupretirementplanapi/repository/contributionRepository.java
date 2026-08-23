package com.commonwealth.groupretirementplanapi.repository;

import com.commonwealth.groupretirementplanapi.entity.contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface contributionRepository extends JpaRepository<contribution, UUID> {
}
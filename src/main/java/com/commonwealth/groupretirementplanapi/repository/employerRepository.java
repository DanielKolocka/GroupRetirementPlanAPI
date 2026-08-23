package com.commonwealth.groupretirementplanapi.repository;

import com.commonwealth.groupretirementplanapi.entity.employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface employerRepository extends JpaRepository<employer, UUID> {
}
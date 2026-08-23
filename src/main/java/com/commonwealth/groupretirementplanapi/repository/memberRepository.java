package com.commonwealth.groupretirementplanapi.repository;

import com.commonwealth.groupretirementplanapi.entity.member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface memberRepository extends JpaRepository<member, UUID> {
}
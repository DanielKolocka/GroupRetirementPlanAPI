package com.commonwealth.groupretirementplanapi.repository;

import com.commonwealth.groupretirementplanapi.entity.contribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface contributionRepository extends JpaRepository<contribution, UUID> {

    @Query(value = "SELECT * FROM contribution WHERE member_id = :memberId AND source = 'EMPLOYER_MATCH'", nativeQuery = true)
    List<contribution> findEmployerMatchContributionsByMemberId(@Param("memberId") UUID memberId);
}
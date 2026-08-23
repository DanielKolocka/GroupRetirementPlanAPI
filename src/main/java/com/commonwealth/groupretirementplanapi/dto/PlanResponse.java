package com.commonwealth.groupretirementplanapi.dto;

import com.commonwealth.groupretirementplanapi.entity.PlanType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        UUID employer_id,
        String name,
        PlanType plan_type,
        BigDecimal match_percentage,
        BigDecimal match_cap_percentage,
        Instant created_at
) { }
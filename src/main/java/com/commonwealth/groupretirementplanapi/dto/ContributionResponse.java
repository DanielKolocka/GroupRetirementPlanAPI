package com.commonwealth.groupretirementplanapi.dto;

import com.commonwealth.groupretirementplanapi.entity.ContributionSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ContributionResponse(
        UUID id,
        UUID member_id,
        BigDecimal amount,
        ContributionSource source,
        LocalDate pay_period_start,
        LocalDate pay_period_end,
        UUID linked_contribution_id,
        Instant created_at
) { }
package com.commonwealth.groupretirementplanapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContributionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotNull LocalDate pay_period_start,
        @NotNull LocalDate pay_period_end
) { }
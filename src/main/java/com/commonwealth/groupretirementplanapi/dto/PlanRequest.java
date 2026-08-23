package com.commonwealth.groupretirementplanapi.dto;

import com.commonwealth.groupretirementplanapi.entity.PlanType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank String name,
        @NotNull PlanType plan_type,

        @NotNull
        @DecimalMin(value = "0", inclusive = true)
        @DecimalMax(value = "100", inclusive = true)
        BigDecimal match_percentage,

        @NotNull
        @DecimalMin(value = "0", inclusive = true)
        @DecimalMax(value = "100", inclusive = true)
        BigDecimal match_cap_percentage

) { }
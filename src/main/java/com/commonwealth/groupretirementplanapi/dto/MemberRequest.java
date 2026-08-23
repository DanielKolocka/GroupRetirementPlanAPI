package com.commonwealth.groupretirementplanapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MemberRequest(
        @NotBlank String first_name,
        @NotBlank String last_name,
        @NotBlank @Email String email,
        @NotNull @Positive BigDecimal annual_salary
) { }
package com.commonwealth.groupretirementplanapi.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployerRequest(
        @NotBlank String name,
        String industry
) { }

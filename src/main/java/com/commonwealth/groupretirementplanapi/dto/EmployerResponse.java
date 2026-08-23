package com.commonwealth.groupretirementplanapi.dto;

import java.time.Instant;
import java.util.UUID;

public record EmployerResponse(
        UUID id,
        String name,
        String industry,
        Instant created_at
) { }

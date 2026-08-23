package com.commonwealth.groupretirementplanapi.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse(
        UUID id,
        UUID employer_id,
        UUID plan_id,
        String first_name,
        String last_name,
        String email,
        BigDecimal annual_salary,
        LocalDate enrollment_date,
        Instant created_at
) { }
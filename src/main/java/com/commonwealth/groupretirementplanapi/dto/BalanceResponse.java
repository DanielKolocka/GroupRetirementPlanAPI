package com.commonwealth.groupretirementplanapi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(
        UUID member_id,
        BigDecimal total_employee_contributions,
        BigDecimal total_employer_match,
        BigDecimal total_balance
) {}

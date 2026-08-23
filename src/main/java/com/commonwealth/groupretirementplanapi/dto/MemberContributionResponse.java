package com.commonwealth.groupretirementplanapi.dto;

public record MemberContributionResponse(
        ContributionResponse employee_contribution,
        ContributionResponse emmployer_match_contribution
) {}

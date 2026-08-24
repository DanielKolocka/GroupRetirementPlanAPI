package com.commonwealth.groupretirementplanapi.dto;

public record MemberContributionResponse(
        ContributionResponse employee_contribution,
        ContributionResponse employer_match_contribution
) {}

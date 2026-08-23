package com.commonwealth.groupretirementplanapi.dto;

import java.util.List;

public record MemberContributionsResponse(
        List<ContributionResponse> employee_contributions,
        List<ContributionResponse> employer_match_contributions
) {}

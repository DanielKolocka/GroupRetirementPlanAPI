package com.commonwealth.groupretirementplanapi.controller;

import com.commonwealth.groupretirementplanapi.dto.ContributionRequest;
import com.commonwealth.groupretirementplanapi.dto.MemberContributionResponse;
import com.commonwealth.groupretirementplanapi.service.ContributionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/members/{memberId}/contributions")
    public ResponseEntity<MemberContributionResponse> createContribution(@PathVariable UUID memberId, @Valid @RequestBody ContributionRequest request) {
        MemberContributionResponse created = contributionService.createContribution(request, memberId, request.pay_period_start(), request.pay_period_end());
        return ResponseEntity.status(201).body(created);
    }
}
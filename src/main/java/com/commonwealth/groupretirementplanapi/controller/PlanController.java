package com.commonwealth.groupretirementplanapi.controller;

import com.commonwealth.groupretirementplanapi.dto.PlanRequest;
import com.commonwealth.groupretirementplanapi.dto.PlanResponse;
import com.commonwealth.groupretirementplanapi.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/employers/{employerId}/plans")
    public ResponseEntity<PlanResponse> createPlan(@PathVariable UUID employerId, @Valid @RequestBody PlanRequest request) {
        PlanResponse created = planService.createPlan(request, employerId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/employers/{employerId}/plans")
    public ResponseEntity<List<PlanResponse>> getPlansByEmployer(@PathVariable UUID employerId) {
        return ResponseEntity.status(200).body(planService.getPlans(employerId));
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<PlanResponse> getPlan(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(planService.getPlan(id));
    }
}
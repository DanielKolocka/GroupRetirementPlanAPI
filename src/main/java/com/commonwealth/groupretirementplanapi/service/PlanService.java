package com.commonwealth.groupretirementplanapi.service;

import com.commonwealth.groupretirementplanapi.dto.EmployerRequest;
import com.commonwealth.groupretirementplanapi.dto.EmployerResponse;
import com.commonwealth.groupretirementplanapi.dto.PlanRequest;
import com.commonwealth.groupretirementplanapi.dto.PlanResponse;
import com.commonwealth.groupretirementplanapi.entity.employer;
import com.commonwealth.groupretirementplanapi.entity.plan;
import com.commonwealth.groupretirementplanapi.exception.NotFoundException;
import com.commonwealth.groupretirementplanapi.repository.planRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanService {

    private final planRepository planRepository;
    private final EmployerService employerService;

    public PlanService(planRepository planRepository, EmployerService employerService) {
        this.planRepository = planRepository;
        this.employerService = employerService;
    }

    public PlanResponse createPlan(PlanRequest request, UUID employer_id) {
        EmployerResponse employer = employerService.getById(employer_id);
        plan plan = new plan(employer.id(), request.name(), request.plan_type(), request.match_percentage(), request.match_cap_percentage());
        plan saved = planRepository.save(plan);
        plan reloaded = planRepository.findById(saved.getId())
                .orElseThrow(() -> new NotFoundException("Plan not found: " + saved.getId()));
        return new PlanResponse(reloaded.getId(), reloaded.getEmployerId(), reloaded.getName(), reloaded.getPlanType(), reloaded.getMatchPercentage(), reloaded.getMatchCapPercentage(), reloaded.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> getPlans(UUID employer_id) {
        EmployerResponse employer = employerService.getById(employer_id);
        List<plan> plans =  planRepository.findByEmployerId(employer.id());
        List<PlanResponse> planResponses = new ArrayList<>();

        for (plan plan : plans) {
            planResponses.add(new PlanResponse(plan.getId(), plan.getEmployerId(), plan.getName(), plan.getPlanType(), plan.getMatchPercentage(), plan.getMatchCapPercentage(), plan.getCreatedAt()));
        }
        return planResponses;
    }

    @Transactional(readOnly = true)
    public PlanResponse getPlan(UUID plan_id) {
        plan plan = planRepository.findById(plan_id).orElseThrow(() -> new NotFoundException("Plan not found: " + plan_id));
        return new PlanResponse(plan.getId(), plan.getEmployerId(), plan.getName(), plan.getPlanType(), plan.getMatchPercentage(), plan.getMatchCapPercentage(), plan.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public plan getPlanById(UUID plan_id) {
        return planRepository.findById(plan_id)
                .orElseThrow(() -> new NotFoundException("Plan not found: " + plan_id));
    }

}

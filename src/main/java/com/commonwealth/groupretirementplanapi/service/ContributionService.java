package com.commonwealth.groupretirementplanapi.service;

import com.commonwealth.groupretirementplanapi.dto.*;
import com.commonwealth.groupretirementplanapi.entity.ContributionSource;
import com.commonwealth.groupretirementplanapi.entity.contribution;
import com.commonwealth.groupretirementplanapi.entity.member;
import com.commonwealth.groupretirementplanapi.entity.plan;
import com.commonwealth.groupretirementplanapi.exception.NotFoundException;
import com.commonwealth.groupretirementplanapi.repository.contributionRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class ContributionService {

    private final contributionRepository contributionRepository;
    private final MemberService memberService;
    private final PlanService planService;
    private final EmployerService employerService;

    public ContributionService(contributionRepository contributionRepository, MemberService memberService, PlanService planService, EmployerService employerService) {
        this.contributionRepository = contributionRepository;
        this.memberService = memberService;
        this.planService = planService;
        this.employerService = employerService;
    }

    public MemberContributionResponse createContribution(ContributionRequest request, UUID member_id, LocalDate payPeriodStart, LocalDate payPeriodEnd) {
        member member = memberService.getMemberById(member_id);
        plan plan = planService.getPlanById(member.getPlanId());
        contribution contribution = new contribution(member_id, request.amount(), ContributionSource.EMPLOYEE, payPeriodStart, payPeriodEnd, null);
        contribution saved = contributionRepository.save(contribution);
        contribution reloaded = contributionRepository.findById(saved.getId())
                .orElseThrow(() -> new NotFoundException("Contribution not found: " + saved.getId()));

        ContributionResponse employee_contribution = new ContributionResponse(reloaded.getId(), reloaded.getMemberId(), reloaded.getAmount(), reloaded.getSource(), reloaded.getPayPeriodStart(), reloaded.getPayPeriodEnd(), null, reloaded.getCreatedAt());


//        Check if contribution is created between payPeriodStart and payPeriodEnd
        LocalDate contributionDate = reloaded.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean inRange = !contributionDate.isBefore(payPeriodStart) && !contributionDate.isAfter(payPeriodEnd);

        BigDecimal proposedMatch = request.amount().multiply(plan.getMatchPercentage()).divide(BigDecimal.valueOf(100));
        BigDecimal annualCapDollars = member.getAnnualSalary().multiply(plan.getMatchCapPercentage()).divide(BigDecimal.valueOf(100));

        List<contribution> employerContributions = contributionRepository.findEmployerMatchContributionsByMemberId(member_id);
        BigDecimal totalEmployerMatch = BigDecimal.ZERO;
        for (contribution employerContribution : employerContributions) {
            totalEmployerMatch = totalEmployerMatch.add(employerContribution.getAmount());
        }
        BigDecimal remainingCapRoom = annualCapDollars.subtract(totalEmployerMatch);
        BigDecimal actualMatch = proposedMatch.min(remainingCapRoom).max(BigDecimal.ZERO);

        ContributionResponse employer_match_contribution = null;
        if (actualMatch.compareTo(BigDecimal.ZERO) > 0 && inRange) {
//            Fetch prior contribution ID and add as linkedContribution
            contribution employerContribution = new contribution(member_id, actualMatch, ContributionSource.EMPLOYER_MATCH, payPeriodStart, payPeriodEnd, reloaded.getId());
            contribution employerReloaded = contributionRepository.findById(employerContribution.getId())
                    .orElseThrow(() -> new NotFoundException("Contribution not found: " + employerContribution.getId()));
            employer_match_contribution = new ContributionResponse(employerReloaded.getId(), employerReloaded.getMemberId(), employerReloaded.getAmount(), employerReloaded.getSource(), employerReloaded.getPayPeriodStart(), employerReloaded.getPayPeriodEnd(), reloaded.getId(), employerReloaded.getCreatedAt());

        }


        return new MemberContributionResponse(employee_contribution, employer_match_contribution);

    }
}
package com.commonwealth.groupretirementplanapi.service;

import com.commonwealth.groupretirementplanapi.dto.MemberRequest;
import com.commonwealth.groupretirementplanapi.dto.MemberResponse;
import com.commonwealth.groupretirementplanapi.dto.PlanResponse;
import com.commonwealth.groupretirementplanapi.entity.member;
import com.commonwealth.groupretirementplanapi.exception.NotFoundException;
import com.commonwealth.groupretirementplanapi.repository.memberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    private final memberRepository memberRepository;
    private final PlanService planService;
    private final EmployerService employerService;

    public MemberService(memberRepository memberRepository, PlanService planService, EmployerService employerService) {
        this.memberRepository = memberRepository;
        this.planService = planService;
        this.employerService = employerService;
    }

    public MemberResponse createMember(MemberRequest request, UUID planId) {
        PlanResponse plan = planService.getPlan(planId);

        member member = new member(plan.employer_id(), planId, request.first_name(), request.last_name(), request.email(), request.annual_salary(), LocalDate.now());
        member saved = memberRepository.save(member);
        member reloaded = memberRepository.findById(saved.getId())
                .orElseThrow(() -> new NotFoundException("Member not found: " + saved.getId()));

        return new MemberResponse(reloaded.getId(), reloaded.getEmployerId(), reloaded.getPlanId(), reloaded.getFirstName(), reloaded.getLastName(), reloaded.getEmail(), reloaded.getAnnualSalary(), reloaded.getEnrollmentDate(), reloaded.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByEmployer(UUID employerId) {
        employerService.getById(employerId); // Verify employer exists

        List<member> members = memberRepository.findByEmployerId(employerId);
        List<MemberResponse> memberResponses = new ArrayList<>();

        for (member member : members) {
            memberResponses.add(new MemberResponse(member.getId(), member.getEmployerId(), member.getPlanId(), member.getFirstName(), member.getLastName(), member.getEmail(), member.getAnnualSalary(), member.getEnrollmentDate(), member.getCreatedAt()));
        }
        return memberResponses;
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(UUID id) {
        member found = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found: " + id));
        return new MemberResponse(found.getId(), found.getEmployerId(), found.getPlanId(), found.getFirstName(), found.getLastName(), found.getEmail(), found.getAnnualSalary(), found.getEnrollmentDate(), found.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public member getMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member not found: " + id));
    }
}
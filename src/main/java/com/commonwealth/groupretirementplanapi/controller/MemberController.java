package com.commonwealth.groupretirementplanapi.controller;

import com.commonwealth.groupretirementplanapi.dto.MemberRequest;
import com.commonwealth.groupretirementplanapi.dto.MemberResponse;
import com.commonwealth.groupretirementplanapi.service.MemberService;
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
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/plans/{planId}/members")
    public ResponseEntity<MemberResponse> createMember(@PathVariable UUID planId, @Valid @RequestBody MemberRequest request) {
        MemberResponse created = memberService.createMember(request, planId);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/employers/{employerId}/members")
    public ResponseEntity<List<MemberResponse>> getMembersByEmployer(@PathVariable UUID employerId) {
        return ResponseEntity.status(200).body(memberService.getMembersByEmployer(employerId));
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(memberService.getMember(id));
    }
}
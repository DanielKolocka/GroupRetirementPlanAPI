package com.commonwealth.groupretirementplanapi.controller;

import com.commonwealth.groupretirementplanapi.dto.EmployerRequest;
import com.commonwealth.groupretirementplanapi.dto.EmployerResponse;
import com.commonwealth.groupretirementplanapi.service.EmployerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @PostMapping
    public ResponseEntity<EmployerResponse> createEmployer(@Valid @RequestBody EmployerRequest request) {
        EmployerResponse created = employerService.createEmployer(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployerResponse> getEmployer(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(employerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmployerResponse>> getAllEmployers() {
        return ResponseEntity.status(200).body(employerService.getAllEmployers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployer(@PathVariable UUID id) {
        employerService.deleteEmployer(id);
        return ResponseEntity.status(204).build();
    }
}
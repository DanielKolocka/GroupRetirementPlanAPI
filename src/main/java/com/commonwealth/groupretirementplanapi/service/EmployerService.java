package com.commonwealth.groupretirementplanapi.service;

import com.commonwealth.groupretirementplanapi.dto.EmployerRequest;
import com.commonwealth.groupretirementplanapi.dto.EmployerResponse;
import com.commonwealth.groupretirementplanapi.entity.employer;
import com.commonwealth.groupretirementplanapi.exception.NotFoundException;
import com.commonwealth.groupretirementplanapi.repository.employerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployerService {

    private final employerRepository employerRepository;

    public EmployerService(employerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    public EmployerResponse createEmployer(EmployerRequest request) {
        employer employer = new employer(request.name(), request.industry());
        employer saved = employerRepository.save(employer);
        employer reloaded = employerRepository.findById(saved.getId())
                .orElseThrow(() -> new NotFoundException("Employer not found with id: " + saved.getId()));
        return new EmployerResponse(reloaded.getId(), reloaded.getName(), reloaded.getIndustry(), reloaded.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public EmployerResponse getById(UUID id) {
        Optional<employer> employer = employerRepository.findById(id);
        employer found = employer.orElseThrow(() -> new NotFoundException("Employer not found with id: " + id));
        return new EmployerResponse(id, found.getName(), found.getIndustry(), found.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<EmployerResponse> getAllEmployers() {
        List<employer> employers = employerRepository.findAll();
        List<EmployerResponse> employerResponses = new ArrayList<>();

        for (employer employer : employers) {
            employerResponses.add(new EmployerResponse(employer.getId(), employer.getName(), employer.getIndustry(), employer.getCreatedAt()));
        }
        return employerResponses;
    }

    @Transactional
    public void deleteEmployer(UUID id) {
        employer found = employerRepository.findById(id).orElseThrow(() -> new NotFoundException("Couldn't delete employer, not found: " + id));
        employerRepository.delete(found);
//        if (!employerRepository.existsById(id)) {
//            throw new NotFoundException("Employer not found: " + id);
//        }
//        employerRepository.deleteById(id);
    }
}
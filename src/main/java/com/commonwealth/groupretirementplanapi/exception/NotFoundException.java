package com.commonwealth.groupretirementplanapi.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}

// Customer message error handling for the service layer
// For example:

//protected Employer findEmployer(UUID id) {
//    return employerRepository.findById(id)
//            .orElseThrow(() -> new NotFoundException("Employer not found: " + id));
//}
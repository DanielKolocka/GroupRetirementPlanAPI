package com.commonwealth.groupretirementplanapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "member")
public class member {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID employer_id;

    @Column(nullable = false)
    private UUID plan_id;

    @Column(nullable = false, length = 50)
    private String first_name;

    @Column(nullable = false, length = 50)
    private String last_name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annual_salary;

    @Column(insertable = false, updatable = false)
    private LocalDate enrollment_date;

    @Column(insertable = false, updatable = false)
    private Instant created_at;

    protected member() {}

    public member(UUID employer_id, UUID plan_id, String first_name, String last_name, String email, BigDecimal annual_salary, LocalDate enrollment_date) {
        this.employer_id = employer_id;
        this.plan_id = plan_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.annual_salary = annual_salary;
        this.enrollment_date = enrollment_date;
    }

    //    Getters
    public UUID getId() {
        return id;
    }

    public UUID getEmployerId() {
        return employer_id;
    }

    public UUID getPlanId() {
        return plan_id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getAnnualSalary() {
        return annual_salary;
    }

    public LocalDate getEnrollmentDate() {
        return enrollment_date;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

//    Setters
    public void setEmployerId(UUID employer_id) {
        this.employer_id = employer_id;
    }

    public void setPlanId(UUID plan_id) {
        this.plan_id = plan_id;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnnualSalary(BigDecimal annual_salary) {
        this.annual_salary = annual_salary;
    }

    public void setEnrollmentDate(LocalDate enrollment_date) {
        this.enrollment_date = enrollment_date;
    }

}
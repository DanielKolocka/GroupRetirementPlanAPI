package com.commonwealth.groupretirementplanapi.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "plan")
public class plan {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID employer_id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private PlanType plan_type;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal match_percentage;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal match_cap_percentage;

    @Column(insertable = false, updatable = false)
    private Instant created_at;

    protected plan() {}

    public plan(UUID employer_id, String name, PlanType plan_type, BigDecimal match_percentage, BigDecimal match_cap_percentage) {
        this.employer_id = employer_id;
        this.name = name;
        this.plan_type = plan_type;
        this.match_percentage = match_percentage;
        this.match_cap_percentage = match_cap_percentage;
    }

    //    Getters
    public UUID getId() {
        return id;
    }

    public UUID getEmployerId() {
        return employer_id;
    }

    public String getName() {
        return name;
    }

    public PlanType getPlanType() {
        return plan_type;
    }

    public BigDecimal getMatchPercentage() {
        return match_percentage;
    }

    public BigDecimal getMatchCapPercentage() {
        return match_cap_percentage;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

//    Setters
    public void setEmployerId(UUID employer_id) {
        this.employer_id = employer_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanType(PlanType plan_type) {
        this.plan_type = plan_type;
    }

    public void setMatchPercentage(BigDecimal match_percentage) {
        this.match_percentage = match_percentage;
    }

    public void setMatchCapPercentage(BigDecimal match_cap_percentage) {
        this.match_cap_percentage = match_cap_percentage;
    }

}

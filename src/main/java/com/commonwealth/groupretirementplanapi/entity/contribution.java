package com.commonwealth.groupretirementplanapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contribution")
public class contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID member_id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private ContributionSource source;

    @Column(nullable = false)
    private LocalDate pay_period_start;

    @Column(nullable = false)
    private LocalDate pay_period_end;

    @Column
    private UUID linked_contribution_id;

    @Column(insertable = false, updatable = false)
    private Instant created_at;

    protected contribution() {}

    public contribution(UUID member_id, BigDecimal amount, ContributionSource source, LocalDate pay_period_start, LocalDate pay_period_end, UUID linked_contribution_id) {
        this.member_id = member_id;
        this.amount = amount;
        this.source = source;
        this.pay_period_start = pay_period_start;
        this.pay_period_end = pay_period_end;
        this.linked_contribution_id = linked_contribution_id;
    }

    //    Getters
    public UUID getId() {
        return id;
    }

    public UUID getMemberId() {
        return member_id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ContributionSource getSource() {
        return source;
    }

    public LocalDate getPayPeriodStart() {
        return pay_period_start;
    }

    public LocalDate getPayPeriodEnd() {
        return pay_period_end;
    }

    public UUID getLinkedContributionId() {
        return linked_contribution_id;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

//    Setters
    public void setMemberId(UUID member_id) {
        this.member_id = member_id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setSource(ContributionSource source) {
        this.source = source;
    }

    public void setPayPeriodStart(LocalDate pay_period_start) {
        this.pay_period_start = pay_period_start;
    }

    public void setPayPeriodEnd(LocalDate pay_period_end) {
        this.pay_period_end = pay_period_end;
    }

    public void setLinkedContributionId(UUID linked_contribution_id) {
        this.linked_contribution_id = linked_contribution_id;
    }

}
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
@Table(name = "contribution")
public class contribution {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID member_id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private ContributionSource source;

    @Column(insertable = false, updatable = false)
    private Instant created_at;

    protected contribution() {}

    public contribution(UUID member_id, BigDecimal amount, ContributionSource source) {
        this.member_id = member_id;
        this.amount = amount;
        this.source = source;
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

}
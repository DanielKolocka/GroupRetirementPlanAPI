package com.commonwealth.groupretirementplanapi.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "employer")
public class employer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String industry;

    @Column(insertable = false, updatable = false)
    private Instant created_at;

    protected employer() {}

    public employer(String name, String industry) {
        this.name = name;
        this.industry = industry;
    }

    //    Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    public Instant getCreatedAt() {
        return created_at;
    }

//    Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

}

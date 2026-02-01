package com.carlosedolv.emergy_api.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "simulations")
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double liters;
    private String type;
    private Double result;
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Simulation() {
    }

    public Simulation(String title, Double liters, String type, Double result, User user) {
        this.title = title;
        this.liters = liters;
        this.type = type;
        this.result = result;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getLiters() {
        return liters;
    }

    public String getType() {
        return type;
    }

    public Double getResult() {
        return result;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLiters(Double liters) {
        this.liters = liters;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

}

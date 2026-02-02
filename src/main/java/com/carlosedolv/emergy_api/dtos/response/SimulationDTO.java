package com.carlosedolv.emergy_api.dtos.response;

import com.carlosedolv.emergy_api.entities.Simulation;

public record SimulationDTO(Long id, String title, Double liters, String type, Double result, UserDTO user) {
    public SimulationDTO(Simulation entity) {
        this(entity.getId(), entity.getTitle(), entity.getLiters(), entity.getType(), entity.getResult(), new UserDTO(entity.getUser()));
    }
}
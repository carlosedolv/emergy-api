package com.carlosedolv.emergy_api.dtos.response;

import com.carlosedolv.emergy_api.entities.Simulation;
import jakarta.validation.constraints.*;

public record SimulationResponseDTO(
        Long id, String title, Double liters, String type, Double result, UserResponseDTO user
) {
    public SimulationResponseDTO(Simulation entity) {
        this(
                entity.getId(),
                entity.getTitle(),
                entity.getLiters(),
                entity.getType(),
                entity.getResult(),
                new UserResponseDTO(entity.getUser())
        );
    }
}
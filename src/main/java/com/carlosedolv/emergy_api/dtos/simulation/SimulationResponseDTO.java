package com.carlosedolv.emergy_api.dtos.simulation;

import com.carlosedolv.emergy_api.domain.entities.Simulation;
import com.carlosedolv.emergy_api.dtos.user.UserResponseDTO;

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
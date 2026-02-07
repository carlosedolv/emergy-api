package com.carlosedolv.emergy_api.dtos.response;

import com.carlosedolv.emergy_api.entities.User;

import java.time.LocalDate;

public record UserResponseDTO(Long id, String name, String email, LocalDate birthday) {
    public UserResponseDTO(User entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), entity.getBirthday());
    }
}
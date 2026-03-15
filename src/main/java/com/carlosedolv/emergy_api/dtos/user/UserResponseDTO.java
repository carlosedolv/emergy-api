package com.carlosedolv.emergy_api.dtos.user;

import com.carlosedolv.emergy_api.domain.entities.User;
import com.carlosedolv.emergy_api.domain.enums.UserRole;

import java.time.LocalDate;

public record UserResponseDTO(Long id, String name, String email, LocalDate birthday, UserRole role) {
    public UserResponseDTO(User entity) {
        this(entity.getId(), entity.getName(), entity.getEmail(), entity.getBirthday(), entity.getRole());
    }
}
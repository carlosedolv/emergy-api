package com.carlosedolv.emergy_api.dtos.request;

import java.time.LocalDate;

public record UserInsertDTO(String name, String email, String password, LocalDate birthday) {
}
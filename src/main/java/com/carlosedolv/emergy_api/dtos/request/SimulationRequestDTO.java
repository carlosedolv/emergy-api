package com.carlosedolv.emergy_api.dtos.request;

import jakarta.validation.constraints.*;

public record SimulationRequestDTO(
        @NotBlank(message = "O titulo é obrigatório")
        @Size(min = 3, max = 80, message = "O titulo deve ter entre 3 e 80 caracteres")
        String title,

        @NotNull(message = "A quantidade de litros é obrigatória")
        @Positive(message = "A quantidade de litros deve ser maior que zero")
        Double liters,

        @NotBlank(message = "O tipo de combustível é obrigatório")
        String type,

        @NotNull(message = "O resultado da simulação é obrigatório")
        @PositiveOrZero(message = "O resultado não pode ser negativo")
        Double result,

        @NotNull(message = "O id do usuário é obrigatório.")
        Long userId
) {

}
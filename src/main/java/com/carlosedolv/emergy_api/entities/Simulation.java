package com.carlosedolv.emergy_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "simulations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 80, message = "O título deve ter entre 3 e 80 caracteres")
    private String title;

    @NotNull(message = "A quantidade de litros é obrigatória")
    @Positive(message = "A quantidade de litros deve ser maior que zero")
    private Double liters;

    @NotBlank(message = "O tipo de combustível é obrigatório")
    private String type;

    @NotNull(message = "O resultado da simulação é obrigatório")
    @PositiveOrZero(message = "O resultado não pode ser negativo")
    private Double result;

    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @NotNull(message = "A simulação deve estar vinculada a um usuário")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simulation that = (Simulation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

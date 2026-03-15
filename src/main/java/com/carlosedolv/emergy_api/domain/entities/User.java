package com.carlosedolv.emergy_api.domain.entities;

import com.carlosedolv.emergy_api.domain.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 20, message = "O nome deve ter entre 3 e 20 caracteres")
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, message = "A senha deve ter no mínimo 4 caracteres")
    private String password;

    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate birthday;

    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    @NotNull
    private UserRole role;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Simulation> simulations = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN) return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")
        );
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail());
    }

}

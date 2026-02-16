package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Carlos")
                .email("carlos@email.com")
                .password("1234")
                .birthday(LocalDate.of(2004, 8, 21))
                .build();
    }

    @Test
    @DisplayName("Deve salvar um usuário com sucesso")
    void testSave() {
        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Carlos");
        assertThat(savedUser.getEmail()).isEqualTo("carlos@email.com");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void testFindAll() {
        User user2 = User.builder()
                .name("Maria")
                .email("maria@email.com")
                .password("5678")
                .birthday(LocalDate.of(1992, 4, 12))
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getName).contains("Carlos", "Maria");
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void testFindById() {
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Deve retornar true quando email existe")
    void testExistsByEmail_WhenEmailExists() {
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("carlos@email.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void testExistsByEmail_WhenEmailDoesNotExists() {
        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("naoexiste@email.com");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDelete() {
        User savedUser = userRepository.save(user);
        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(savedUser.getId());

        assertThat(deletedUser).isEmpty();
    }
}

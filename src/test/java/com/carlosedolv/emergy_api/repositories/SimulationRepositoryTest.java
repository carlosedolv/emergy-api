package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.entities.Simulation;
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
public class SimulationRepositoryTest {
    @Autowired
    private SimulationRepository simulationRepository;

    @Autowired
    private UserRepository userRepository;

    private Simulation simulation;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("Carlos")
                .email("carlos@email.com")
                .password("1234")
                .birthday(LocalDate.of(2004, 8, 21))
                .build();
        userRepository.save(owner);

        simulation = Simulation.builder()
                .title("Teste")
                .liters(24.43)
                .type("Etanol")
                .result(120.02)
                .user(owner)
                .build();
    }

    @Test
    @DisplayName("Deve salvar uma simulação com sucesso")
    void testSave() {
        Simulation savedSimulation = simulationRepository.save(simulation);

        assertThat(savedSimulation).isNotNull();
        assertThat(savedSimulation.getId()).isNotNull();
        assertThat(savedSimulation.getTitle()).isEqualTo("Teste");
        assertThat(savedSimulation.getCreatedAt()).isNotNull();

        assertThat(savedSimulation.getUser().getId()).isEqualTo(owner.getId());
        assertThat(savedSimulation.getUser().getId()).isEqualTo(owner.getId());
    }

    @Test
    @DisplayName("Deve listar todas as simulações")
    void testFindAll() {
        Simulation simulation2 = Simulation.builder()
                .title("Teste2")
                .liters(1.43)
                .type("Gasolina")
                .result(20.32)
                .user(owner)
                .build();

        simulationRepository.save(simulation);
        simulationRepository.save(simulation2);

        var simulations = simulationRepository.findAll();

        assertThat(simulations).hasSize(2);
        assertThat(simulations).extracting(Simulation::getTitle).contains("Teste", "Teste2");
    }

    @Test
    @DisplayName("Deve buscar simulação por ID")
    void testFindById() {
        Simulation savedSimulation = simulationRepository.save(simulation);
        Optional<Simulation> foundSimulation = simulationRepository.findById(savedSimulation.getId());

        assertThat(foundSimulation).isPresent();
        assertThat(foundSimulation.get().getId()).isEqualTo(savedSimulation.getId());
    }

    @Test
    @DisplayName("Deve procurar simulações pelo título com sucesso")
    void testFindByTitle_WhenTitleExists() {
        Simulation simulation2 = Simulation.builder()
                .title("Teste")
                .liters(4.43)
                .type("Gasolina")
                .result(20.34)
                .user(owner)
                .build();

        simulationRepository.save(simulation);
        simulationRepository.save(simulation2);

        List<Simulation> simulations = simulationRepository.findByTitle("Teste");

        assertThat(simulations)
                .isNotEmpty()
                .hasSize(2)
                .extracting(Simulation::getTitle)
                .containsOnly("Teste");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando título não existir")
    void testFindByTitle_WhenTitleDoesNotExists() {
        simulationRepository.save(simulation);

        List<Simulation> simulations = simulationRepository.findByTitle("NaoExiste");

        assertThat(simulations).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar simulação com sucesso")
    void testDelete() {
        Simulation savedSimulation = simulationRepository.save(simulation);
        simulationRepository.delete(savedSimulation);
        Optional<Simulation> deletedSimulation = simulationRepository.findById(savedSimulation.getId());

        assertThat(deletedSimulation).isEmpty();
    }
}

package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.request.SimulationRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.SimulationResponseDTO;
import com.carlosedolv.emergy_api.entities.Simulation;
import com.carlosedolv.emergy_api.entities.User;
import com.carlosedolv.emergy_api.repositories.SimulationRepository;
import com.carlosedolv.emergy_api.repositories.UserRepository;
import com.carlosedolv.emergy_api.services.exceptions.ResourceDataIntegrityException;
import com.carlosedolv.emergy_api.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SimulationServiceTest {
    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SimulationService simulationService;

    private Simulation simulation;
    private SimulationRequestDTO simulationRequestDTO;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("Carlos")
                .email("carlos@email.com")
                .password("1234")
                .birthday(LocalDate.of(2004, 8, 21))
                .build();

        simulation = Simulation.builder()
                .id(1L)
                .title("Teste")
                .liters(24.43)
                .type("Etanol")
                .result(120.02)
                .user(owner)
                .build();

        simulationRequestDTO = new SimulationRequestDTO(
                simulation.getTitle(),
                simulation.getLiters(),
                simulation.getType(),
                simulation.getResult(),
                owner.getId()
        );
    }

    @Test
    @DisplayName("Deve retornar lista de todas as simulações")
    void testFindAll() {
        Simulation simulation2 = Simulation.builder()
                .title("Teste2")
                .liters(2.43)
                .type("Etanol")
                .result(12120.02)
                .user(owner)
                .build();

        // Arrange
        when(simulationRepository.findAll()).thenReturn(Arrays.asList(simulation, simulation2));

        // Act
        List<SimulationResponseDTO> result = simulationService.findAll();

        // Assert & Verify
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Teste");
        assertThat(result.get(1).title()).isEqualTo("Teste2");
        verify(simulationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar simulação por ID")
    void testFindById_Success() {
        // Arrange
        when(simulationRepository.findById(1L)).thenReturn(Optional.of(simulation));

        // Act
        SimulationResponseDTO result = simulationService.findById(1L);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Teste");
        verify(simulationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando simulação não existe")
    void testFindById_NotFound() {
        // Arrange
        when(simulationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> simulationService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        // Assert & Verify
        verify(simulationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve buscar simulações por título")
    void testFindByTitle() {
        Simulation simulation2 = Simulation.builder()
                .id(2L)
                .title("Teste")
                .liters(24.43)
                .type("Gasolina")
                .result(666.5)
                .user(owner)
                .build();

        // Arrange
        when(simulationRepository.findByTitle("Teste")).thenReturn(Arrays.asList(simulation, simulation2));

        // Act
        List<SimulationResponseDTO> result = simulationService.findByTitle("Teste");

        // Assert & Verify
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Teste");
        assertThat(result.get(1).title()).isEqualTo("Teste");
        verify(simulationRepository, times(1)).findByTitle("Teste");
    }

    @Test
    @DisplayName("Deve salvar simulação com sucesso quando o usuário existe")
    void testSave_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(simulationRepository.save(any(Simulation.class))).thenReturn(simulation);

        // Act
        SimulationResponseDTO result = simulationService.save(simulationRequestDTO);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo(simulationRequestDTO.title());
        verify(userRepository, times(1)).findById(1L);
        verify(simulationRepository, times(1)).save(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar se o ID do usuário for nulo")
    void testSave_UserIdNull() {
        // Arrange
        SimulationRequestDTO dtoWithNoUser = new SimulationRequestDTO("Título", 10.0, "Tipo", 100.0, null);

        // Act & Assert
        assertThatThrownBy(() -> simulationService.save(dtoWithNoUser))
                .isInstanceOf(ResourceDataIntegrityException.class);

        // Verify
        verify(simulationRepository, never()).save(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar simulação para usuário inexistente")
    void testSave_UserNotFound() {
        // Arrange
        Long invalidId = 999L;
        SimulationRequestDTO dtoWithInvalidUser = new SimulationRequestDTO("Título", 10.0, "Tipo", 100.0, invalidId);
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> simulationService.save(dtoWithInvalidUser))
                .isInstanceOf(ResourceNotFoundException.class);

        // Verify
        verify(userRepository, times(1)).findById(invalidId);
        verify(simulationRepository, never()).save(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve deletar simulação com sucesso")
    void testDelete_Success() {
        // Arrange
        when(simulationRepository.findById(1L)).thenReturn(Optional.of(simulation));
        doNothing().when(simulationRepository).delete(simulation);

        // Act
        simulationService.delete(1L);

        // Verify
        verify(simulationRepository, times(1)).findById(1L);
        verify(simulationRepository, times(1)).delete(simulation);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar simulação inexistente")
    void testDelete_NotFound() {
        // Arrange
        Long invalidId = 999L;
        when(simulationRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> simulationService.delete(invalidId))
                .isInstanceOf(ResourceNotFoundException.class);

        // Verify
        verify(simulationRepository, times(1)).findById(invalidId);
        verify(simulationRepository, never()).delete(any(Simulation.class));
    }

    @Test
    @DisplayName("Deve atualizar simulação com sucesso")
    void testUpdate_Success() {
        // Arrange
        SimulationRequestDTO dtoUpdate = new SimulationRequestDTO("Teste2", 10.0, "Tipo", 100.0, 1L);
        when(simulationRepository.findById(1L)).thenReturn(Optional.of(simulation));

        // Act
        SimulationResponseDTO result = simulationService.update(1L, dtoUpdate);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo(dtoUpdate.title());
        verify(simulationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar simulação inexistente")
    void testUpdate_NotFound() {
        Long invalidId = 999L;
        when(simulationRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> simulationService.update(invalidId, simulationRequestDTO))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(simulationRepository, times(1)).findById(invalidId);
        verify(simulationRepository, never()).save(any(Simulation.class));
    }
}

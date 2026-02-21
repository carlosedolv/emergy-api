package com.carlosedolv.emergy_api.controllers;

import com.carlosedolv.emergy_api.dtos.request.SimulationRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.SimulationResponseDTO;
import com.carlosedolv.emergy_api.dtos.response.UserResponseDTO;
import com.carlosedolv.emergy_api.entities.Simulation;
import com.carlosedolv.emergy_api.entities.User;
import com.carlosedolv.emergy_api.services.SimulationService;
import com.carlosedolv.emergy_api.services.UserService;
import com.carlosedolv.emergy_api.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class SimulationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SimulationService simulationService;

    private Simulation simulation;
    private User owner;
    private SimulationRequestDTO simulationRequestDTO;
    private SimulationResponseDTO simulationResponseDTO;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("Carlos")
                .email("carlos@email.com")
                .password("1234")
                .birthday(LocalDate.of(2004, 8, 21))
                .build();

        simulation = Simulation.builder()
                .id(1L)
                .title("Teste")
                .liters(242.43)
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

        simulationResponseDTO = new SimulationResponseDTO(simulation);
    }

    @Test
    @DisplayName("GET /simulations - Deve retornar lista de simulações")
    void testFindAll() throws Exception{
        // Assert
        Simulation simulation2 = Simulation.builder()
                .id(2L)
                .title("Teste2")
                .liters(24.43)
                .type("Etanol")
                .result(10.02)
                .user(owner)
                .build();
        when(simulationService.findAll()).thenReturn(List.of(simulationResponseDTO, new SimulationResponseDTO(simulation2)));

        // Act & Assert
        mockMvc.perform(get("/simulations"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Teste"))
                .andExpect(jsonPath("$[1].title").value("Teste2"));

        // Verify
        verify(simulationService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /simulations/{id} - Deve retornar simulação por ID")
    void testFindById_Success() throws Exception {
        // Arrange
        when(simulationService.findById(simulation.getId())).thenReturn(simulationResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/simulations/" + simulation.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Teste"));

        // Verify
        verify(simulationService, times(1)).findById(simulation.getId());
    }

    @Test
    @DisplayName("GET /simulations/{id} - Deve retornar 404 quando simulação não existe")
    void testFindById_NotFound() throws Exception {
        // Arrange
        Long invalidId = 999L;
        when(simulationService.findById(invalidId)).thenThrow(new ResourceNotFoundException(invalidId));

        // Act & Assert
        mockMvc.perform(get("/simulations/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        // Verify
        verify(simulationService, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("GET /simulations/title/{title} - Deve retornar lista de simulações filtradas por título")
    void testFindByTitle_Success() throws Exception {
        // Arrange
        String title = simulation.getTitle();
        Simulation simulation2 = Simulation.builder()
                .id(2L)
                .title(title)
                .liters(24.43)
                .type("Etanol")
                .result(10.02)
                .user(owner)
                .build();
        when(simulationService.findByTitle(title)).thenReturn(List.of(simulationResponseDTO, new SimulationResponseDTO(simulation2)));

        // Act
        mockMvc.perform(get("/simulations/title/" + title)) // A URL segue o seu @GetMapping
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[1].title").value(title));

        // Verify
        verify(simulationService, times(1)).findByTitle(title);
    }

    @Test
    @DisplayName("POST /simulations - Deve criar nova simulação")
    void testSave_Success() throws Exception {
        // Arrange
        when(simulationService.save(any(SimulationRequestDTO.class))).thenReturn(simulationResponseDTO);

        // Act
        mockMvc.perform(post("/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(simulationRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value(simulation.getTitle()));

        // Verify
        verify(simulationService, times(1)).save(any(SimulationRequestDTO.class));
    }

    @Test
    @DisplayName("POST /simulations - Deve retornar 404 quando usuário da simulação não existe")
    void testSave_UserNotFound() throws Exception {
        Long invalidId = 999L;
        when(simulationService.save(any(SimulationRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(invalidId));

        mockMvc.perform(post("/simulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(simulationRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(simulationService, times(1)).save(any(SimulationRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /simulations/{id} - Deve deletar simulação")
    void testDelete_Success() throws Exception {
        doNothing().when(simulationService).delete(simulation.getId());

        mockMvc.perform(delete("/simulations/" + simulation.getId()))
                .andExpect(status().isNoContent());

        verify(simulationService, times(1)).delete(simulation.getId());
    }

    @Test
    @DisplayName("DELETE /simulations/{id} - Deve retornar 404 ao tentar deletar simulação inexistente")
    void testDelete_NotFound() throws Exception {
        Long invalidId = 999L;
        doThrow(new ResourceNotFoundException(invalidId)).when(simulationService).delete(invalidId);

        mockMvc.perform(delete("/simulations/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(simulationService, times(1)).delete(invalidId);
    }

    @Test
    @DisplayName("PUT /simulations/{id} - Deve atualizar simulação")
    void testUpdate_Success() throws Exception {
        Long id = simulation.getId();
        SimulationRequestDTO updateRequest = new SimulationRequestDTO(
                "Teste2",
                234.43,
                "Gasolina",
                120.02,
                simulation.getUser().getId()
        );

        SimulationResponseDTO updateResponse = new SimulationResponseDTO(
                id,
                updateRequest.title(),
                updateRequest.liters(),
                updateRequest.type(),
                updateRequest.result(),
                new UserResponseDTO(owner)
        );

        when(simulationService.update(eq(id), any(SimulationRequestDTO.class))).thenReturn(updateResponse);

        mockMvc.perform(put("/simulations/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updateRequest.title()));

        verify(simulationService, times(1)).update(eq(id), any(SimulationRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /simulations/{id} - Deve retornar 404 quando simulação não existe")
    void testUpdate_NotFound() throws Exception {
        Long invalidId = 999L;
        SimulationRequestDTO updateRequest = new SimulationRequestDTO(
                "Teste2",
                234.43,
                "Gasolina",
                120.02,
                simulation.getUser().getId()
        );
        when(simulationService.update(eq(invalidId), any(SimulationRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(invalidId));

        mockMvc.perform(put("/simulations/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(simulationService, times(1)).update(eq(invalidId), any(SimulationRequestDTO.class));
    }
}

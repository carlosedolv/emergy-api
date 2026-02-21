package com.carlosedolv.emergy_api.controllers;

import com.carlosedolv.emergy_api.dtos.request.UserRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.UserResponseDTO;
import com.carlosedolv.emergy_api.entities.User;
import com.carlosedolv.emergy_api.services.UserService;
import com.carlosedolv.emergy_api.services.exceptions.ResourceDataIntegrityException;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Carlos")
                .email("carlos@test.com")
                .password("1234")
                .birthday(LocalDate.of(2004, 8, 21))
                .build();

        userRequestDTO = new UserRequestDTO(user.getName(), user.getEmail(), user.getPassword(), user.getBirthday());
        userResponseDTO = new UserResponseDTO(user);
    }
    @Test
    @DisplayName("GET /users - Deve retornar lista de usuários")
    void testFindAll() throws Exception {
        // Arrange
        User user2 = User.builder()
                .id(2L)
                .name("Maria")
                .email("maria@test.com")
                .password("5678")
                .birthday(LocalDate.of(2002, 5, 12))
                .build();

        when(userService.findAll()).thenReturn(List.of(userResponseDTO, new UserResponseDTO(user2)));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Carlos"))
                .andExpect(jsonPath("$[1].name").value("Maria"));

        // Verify
        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /users/{id} - Deve retornar usuário por ID")
    void testFindById_Success() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(userResponseDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@test.com"));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /users/{id} - Deve retornar 404 quando usuário não existe")
    void testFindById_NotFound() throws Exception {
        Long invalidId = 999L;
        when(userService.findById(invalidId)).thenThrow(new ResourceNotFoundException(invalidId));

        mockMvc.perform(get("/users/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(userService, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("POST /users - Deve criar novo usuário")
    void testSave_Success() throws Exception {
        when(userService.save(any(UserRequestDTO.class))).thenReturn(userResponseDTO);

        // Act
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlos"));

        // Verify
        verify(userService, times(1)).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("POST /users - Deve retornar 409 quando o email já existe")
    void testSave_EmailAlreadyExists() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDTO.class)))
                .thenThrow(new ResourceDataIntegrityException("Email already exists."));

        // Act
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                // Assert
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Resource data integrity violation"));

        verify(userService, times(1)).save(any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /users/{id} - Deve deletar usuário")
    void testDelete_Success() throws Exception {
        // Arrange
        doNothing().when(userService).delete(user.getId());

        //Act
        mockMvc.perform(delete("/users/" + user.getId()))
                .andExpect(status().isNoContent());

        // Verify
        verify(userService, times(1)).delete(user.getId());
    }

    @Test
    @DisplayName("DELETE /users/{id} - Deve retornar 404 ao tentar deletar usuário inexistente")
    void testDelete_NotFound() throws Exception {
        // Arrange
        Long invalidId = 999L;
        doThrow(new ResourceNotFoundException(invalidId))
                .when(userService).delete(invalidId);

        // Act & Assert
        mockMvc.perform(delete("/users/" + invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        // Verify
        verify(userService, times(1)).delete(invalidId);
    }

    @Test
    @DisplayName("PUT /users/{id} - Deve atualizar usuário")
    void testUpdate_Success() throws Exception {
        // Arrange
        Long id = 1L;
        UserRequestDTO updateRequest = new UserRequestDTO(
                "Maria",
                "maria@test.com",
                "56789",
                LocalDate.of(2002, 8, 21)
        );
        UserResponseDTO updateResponse = new UserResponseDTO(
                id,
                updateRequest.name(),
                updateRequest.email(),
                updateRequest.birthday()
        );

        when(userService.update(eq(id), any(UserRequestDTO.class))).thenReturn(updateResponse);

        // Act
        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria"));

        verify(userService, times(1)).update(eq(id), any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - Deve retornar 404 quando usuário que não existe")
    void testUpdate_NotFound() throws Exception {
        Long id = 999L;
        UserRequestDTO updateRequest = new UserRequestDTO(
                "Maria",
                "maria@test.com",
                "56789",
                LocalDate.of(2002, 8, 21)
        );

        when(userService.update(eq(id), any(UserRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(id));

        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"));

        verify(userService, times(1)).update(eq(id), any(UserRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /users/{id} - Deve retornar 409 quando email já existe")
    void testUpdate_EmailAlreadyExists() throws Exception {
        Long id = 1L;
        UserRequestDTO updateWithDuplicateEmail = new UserRequestDTO(
                "Maria",
                "carlos@test.com",
                "56789",
                LocalDate.of(2002, 8, 21)
        );

        when(userService.update(eq(id), any(UserRequestDTO.class)))
                .thenThrow(new ResourceDataIntegrityException("Email already exists."));

        mockMvc.perform(put("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateWithDuplicateEmail)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Resource data integrity violation"));

        verify(userService, times(1)).update(eq(id), any(UserRequestDTO.class));
    }
}

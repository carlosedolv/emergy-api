package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.request.UserRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.UserResponseDTO;
import com.carlosedolv.emergy_api.entities.User;
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
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDTO userRequestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Carlos")
                .email("carlos@test.com")
                .password("123")
                .birthday(LocalDate.of(1995, 5, 15))
                .build();

        userRequestDTO = new UserRequestDTO(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getBirthday()
        );
    }

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void testFindAll() {
        // Arrange
        User user2 = User.builder()
                .id(2L)
                .name("Maria")
                .email("maria@test.com")
                .password("12345")
                .birthday(LocalDate.of(2001, 5, 15))
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert & Verify
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Carlos");
        assertThat(result.get(1).name()).isEqualTo("Maria");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void testFindById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.findById(1L);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Carlos");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void testFindById_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        // Verify
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve inserir novo usuário com sucesso")
    void testSave_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponseDTO result = userService.save(userRequestDTO);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Carlos");
        assertThat(result.email()).isEqualTo("carlos@test.com");
        verify(userRepository, times(1)).existsByEmail("carlos@test.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email já estiver cadastrado")
    void testSave_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("carlos@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.save(userRequestDTO))
                .isInstanceOf(ResourceDataIntegrityException.class);

        // Verify
        verify(userRepository, times(1)).existsByEmail("carlos@test.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDelete_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        userService.delete(1L);

        // Verify
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void testDelete_NotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        // Verify
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testUpdate_Success() {
        UserRequestDTO update = new UserRequestDTO(
                "Maria",
                "maria@test.com",
                "1234",
                LocalDate.of(1995, 5, 15)
        );

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponseDTO result = userService.update(1L, update);

        // Assert & Verify
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Maria");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já existente")
    void testUpdate_EmailAlreadyExists() {
        UserRequestDTO update = new UserRequestDTO(
                "Maria",
                "outro@test.com",
                "1234",
                LocalDate.of(1995, 5, 15)
        );

        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.update(1L, update))
                .isInstanceOf(ResourceDataIntegrityException.class);

        // Verify
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("outro@test.com");
        verify(userRepository, never()).save(any(User.class));
    }
}

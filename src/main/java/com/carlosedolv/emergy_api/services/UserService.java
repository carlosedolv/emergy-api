package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.request.UserRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.UserResponseDTO;
import com.carlosedolv.emergy_api.entities.User;
import com.carlosedolv.emergy_api.repositories.UserRepository;
import com.carlosedolv.emergy_api.services.exceptions.ResourceDataIntegrityException;
import com.carlosedolv.emergy_api.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public List<UserResponseDTO> findAll(){
        return repository.findAll().stream().map(UserResponseDTO::new).toList();
    }

    public UserResponseDTO findById(Long id) {
        return new UserResponseDTO(
                repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id))
        );
    }

    public UserResponseDTO findByEmail(String email) {
        return new UserResponseDTO(
                repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(email))
        );
    }

    public UserResponseDTO save(UserRequestDTO dto) {
        if(repository.existsByEmail(dto.email())) {
            throw new ResourceDataIntegrityException("Email already exists.");
        }
        User entity = repository.save(copyDtoToEntity(dto));
        return new UserResponseDTO(entity);
    }

    public void delete(Long id) {
        try {
            User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            repository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceDataIntegrityException("Violations of database restrictions.");
        }
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        if(!entity.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new ResourceDataIntegrityException("Email is already in use.");
        }
        updateEntity(entity, dto);
        return new UserResponseDTO(entity);
    }

    private User copyDtoToEntity(UserRequestDTO dto) {
        User entity = new User();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setBirthday(dto.birthday());
        return entity;
    }

    private void updateEntity(User entity, UserRequestDTO dto) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setBirthday(dto.birthday());
    }

}

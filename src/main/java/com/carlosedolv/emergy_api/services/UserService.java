package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.request.UserInsertDTO;
import com.carlosedolv.emergy_api.dtos.response.UserDTO;
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

    public List<UserDTO> findAll(){
        return repository.findAll().stream().map(UserDTO::new).toList();
    }

    public UserDTO findById(Long id) {
        return new UserDTO(
                repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id))
        );
    }

    public UserDTO findByEmail(String email) {
        return new UserDTO(
                repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(email))
        );
    }

    public UserDTO save(UserInsertDTO dto) {
        if(repository.existsByEmail(dto.email())) {
            throw new ResourceDataIntegrityException("Email already exists.");
        }
        User entity = repository.save(copyDtoToEntity(dto));
        return new UserDTO(entity);
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
    public UserDTO update(Long id, UserInsertDTO dto) {
        User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        if(!entity.getEmail().equals(dto.email()) && repository.existsByEmail(dto.email())) {
            throw new ResourceDataIntegrityException("Email is already in use.");
        }
        updateEntity(entity, dto);
        return new UserDTO(entity);
    }

    private void updateEntity(User entity, UserInsertDTO dto) {
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setBirthday(dto.birthday());
    }

    private User copyDtoToEntity(UserInsertDTO dto) {
        User entity = new User();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setBirthday(dto.birthday());
        return entity;
    }
}

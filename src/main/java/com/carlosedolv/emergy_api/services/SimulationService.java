package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.request.SimulationRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.SimulationResponseDTO;
import com.carlosedolv.emergy_api.entities.Simulation;
import com.carlosedolv.emergy_api.entities.User;
import com.carlosedolv.emergy_api.repositories.SimulationRepository;
import com.carlosedolv.emergy_api.repositories.UserRepository;
import com.carlosedolv.emergy_api.services.exceptions.ResourceDataIntegrityException;
import com.carlosedolv.emergy_api.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService {
    @Autowired
    private SimulationRepository repository;

    @Autowired
    private UserRepository userRepository;

    public List<SimulationResponseDTO> findAll() {
        return repository.findAll().stream().map(SimulationResponseDTO::new).toList();
    }

    public SimulationResponseDTO findById(Long id) {
        return new SimulationResponseDTO(
                repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id))
        );
    }

    public List<SimulationResponseDTO> findByTitle(String title) {
        return repository.findByTitle(title).stream().map(SimulationResponseDTO::new).toList();
    }

    public SimulationResponseDTO save(SimulationRequestDTO dto) {
        if(dto.userId() == null){
            throw new ResourceDataIntegrityException("User ID is required for simulation.");
        }
        User user  = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.userId()));
        Simulation simulation = repository.save(copyDtoToEntity(dto, user));
        return new SimulationResponseDTO(simulation);
    }

    public void delete(Long id) {
        try {
            Simulation simulation = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            repository.delete(simulation);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceDataIntegrityException("Violation of database restrictions to delete.");
        }
    }

    @Transactional
    public SimulationResponseDTO update(Long id, SimulationRequestDTO dto) {
        try {
            Simulation simulation = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            updateSimulation(dto, simulation);
            return new SimulationResponseDTO(simulation);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceDataIntegrityException("Violations of database restrictions to update.");
        }

    }

    private Simulation copyDtoToEntity(SimulationRequestDTO dto, User user) {
        Simulation simulation = new Simulation();
        simulation.setTitle(dto.title());
        simulation.setLiters(dto.liters());
        simulation.setType(dto.type());
        simulation.setResult(dto.result());
        simulation.setUser(user);
        return simulation;
    }

    private void updateSimulation(SimulationRequestDTO dto, Simulation entity) {
        entity.setTitle(dto.title());
        entity.setLiters(dto.liters());
        entity.setType(dto.type());
        entity.setResult(dto.result());
    }
}

package com.carlosedolv.emergy_api.services;

import com.carlosedolv.emergy_api.dtos.response.SimulationDTO;
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

    public List<SimulationDTO> findAll() {
        return repository.findAll().stream().map(SimulationDTO::new).toList();
    }

    public SimulationDTO findById(Long id) {
        return new SimulationDTO(
                repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id))
        );
    }

    public List<SimulationDTO> findByTitle(String title) {
        return repository.findByTitle(title).stream().map(SimulationDTO::new).toList();
    }

    public SimulationDTO save(SimulationDTO dto) {
        if(dto.user() == null || dto.user().id() == null){
            throw new ResourceDataIntegrityException("User ID is required for simulation.");
        }
        User user  = userRepository.findById(dto.user().id())
                .orElseThrow(() -> new ResourceNotFoundException(dto.user().id()));
        Simulation simulation = repository.save(copyDtoToEntity(dto, user));
        return new SimulationDTO(simulation);
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
    public SimulationDTO update(Long id, SimulationDTO dto) {
        try {
            Simulation simulation = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            updateSimulation(simulation, dto);
            return new SimulationDTO(simulation);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceDataIntegrityException("Violations of database restrictions to update.");
        }

    }

    private Simulation copyDtoToEntity(SimulationDTO dto, User user) {
        Simulation simulation = new Simulation();
        simulation.setTitle(dto.title());
        simulation.setTitle(dto.title());
        simulation.setType(dto.type());
        simulation.setResult(dto.result());
        simulation.setUser(user);
        return simulation;
    }

    private void updateSimulation(Simulation entity, SimulationDTO dto) {
        entity.setTitle(dto.title());
        entity.setLiters(dto.liters());
        entity.setType(dto.type());
        entity.setResult(dto.result());
    }
}

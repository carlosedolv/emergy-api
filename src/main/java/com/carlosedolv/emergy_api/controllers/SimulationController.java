package com.carlosedolv.emergy_api.controllers;

import com.carlosedolv.emergy_api.dtos.request.SimulationRequestDTO;
import com.carlosedolv.emergy_api.dtos.response.SimulationResponseDTO;
import com.carlosedolv.emergy_api.services.SimulationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/simulations")
public class SimulationController {
    private final SimulationService service;

    public SimulationController(SimulationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SimulationResponseDTO>> findAll() {
        List<SimulationResponseDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SimulationResponseDTO> findById(@PathVariable Long id) {
        SimulationResponseDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/title/{title}")
    public ResponseEntity<List<SimulationResponseDTO>> findByTitle(@PathVariable String title) {
        List<SimulationResponseDTO> list = service.findByTitle(title);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<SimulationResponseDTO> save(@Valid @RequestBody SimulationRequestDTO dto) {
        SimulationResponseDTO saved = service.save(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SimulationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SimulationRequestDTO dto) {
        SimulationResponseDTO saved = service.update(id, dto);
        return ResponseEntity.ok().body(saved);
    }

}

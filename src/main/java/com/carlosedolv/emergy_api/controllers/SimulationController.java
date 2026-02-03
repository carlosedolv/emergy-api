package com.carlosedolv.emergy_api.controllers;

import com.carlosedolv.emergy_api.dtos.response.SimulationDTO;
import com.carlosedolv.emergy_api.services.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/simulations")
public class SimulationController {
    @Autowired
    private SimulationService service;

    @GetMapping
    public ResponseEntity<List<SimulationDTO>> getAll() {
        List<SimulationDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<SimulationDTO> findById(@PathVariable Long id) {
        SimulationDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/title/{title}")
    public ResponseEntity<List<SimulationDTO>> findByTitle(@PathVariable String title) {
        List<SimulationDTO> list = service.findByTitle(title);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping
    public ResponseEntity<SimulationDTO> save(@RequestBody SimulationDTO dto) {
        SimulationDTO saved = service.save(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(uri).body(saved);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<SimulationDTO> update(@PathVariable Long id, @RequestBody SimulationDTO dto) {
        SimulationDTO saved = service.update(id, dto);
        return ResponseEntity.ok().body(saved);
    }

}

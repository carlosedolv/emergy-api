package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.entities.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    List<Simulation> findByTitle(String title);
}

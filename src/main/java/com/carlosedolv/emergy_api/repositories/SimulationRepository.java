package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.domain.entities.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
    List<Simulation> findByTitle(String title);
}

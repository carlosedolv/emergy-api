package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.entities.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
}

package com.carlosedolv.emergy_api.repositories;

import com.carlosedolv.emergy_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

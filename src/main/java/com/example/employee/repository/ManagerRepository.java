package com.example.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entities.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {

	Optional<Manager> findByEmail(String email);

}

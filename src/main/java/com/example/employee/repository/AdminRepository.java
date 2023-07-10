package com.example.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}

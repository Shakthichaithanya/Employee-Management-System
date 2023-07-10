package com.example.employee.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long managerId;
	@NotBlank
	private String managerName;
	private String managerDepartment;
	@Email
	@Column(unique = true)
	private String email;
	@OneToMany
	private List<Employee> reportingEmployees;

}

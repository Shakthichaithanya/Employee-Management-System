package com.example.employee.dto;

import java.util.List;

import com.example.employee.entities.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDTO {

	private long managerId;
	private String managerName;
	private String managerDepartment;
	private String email;
	private List<Employee> reportingEmployees;

}

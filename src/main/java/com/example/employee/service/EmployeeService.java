package com.example.employee.service;

import java.util.List;

import com.example.employee.dto.EmployeeDTO;

public interface EmployeeService {

	String addEmployee(EmployeeDTO employee);

	List<EmployeeDTO> getAllEmployees();

	EmployeeDTO getEmployee(String email);

	String updateEmployeeDetails(String email, EmployeeDTO employeeDTO);

	String deleteEmployee(long employeeId);

}

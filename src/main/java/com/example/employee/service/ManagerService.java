package com.example.employee.service;

import java.util.List;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.dto.ManagerDTO;

public interface ManagerService {

	String addManager(ManagerDTO managerDto);

	List<ManagerDTO> getAllManagers();

	ManagerDTO getManager(long managerId);

	String updateManagerDetails(long managerId, ManagerDTO manager);

	String deleteManagerById(long managerId);

	List<EmployeeDTO> getReportingEployees(String email);

	String addReportingEmployees(String email, EmployeeDTO employee);

	ManagerDTO getLoggedInManager(String email);

	String updateLoggedInManager(String email, ManagerDTO managerDto);
}

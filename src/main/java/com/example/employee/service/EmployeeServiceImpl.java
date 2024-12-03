package com.example.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.entities.Employee;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.repository.EmployeeRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private final EmployeeRepository employeeRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper){
		this.employeeRepository = employeeRepository;
		this.modelMapper = modelMapper;
	}

	/**
	 * creating new employee
	 * 
	 * @param EmployeeDTO employeeDto
	 * @return String
	 * @exception ConstraintViolationException
	 */
	@Override
	public String addEmployee(EmployeeDTO employeeDto) {
		Employee employee = modelMapper.map(employeeDto, Employee.class);
		try {
			Employee savedEmployee = employeeRepository.save(employee);
			logger.debug("saved employee: " + savedEmployee);
		} catch (Exception e) {
			throw new ConstraintViolationException("could not execute the statement");
		}
		logger.info("created new employee");
		return "Employee added successfully";
	}

	/**
	 * Fetching all employee details
	 * 
	 * @return List<EmployeeDTO>
	 */
	@Override
	public List<EmployeeDTO> getAllEmployees() {
		List<EmployeeDTO> employees = employeeRepository.findAll().stream()
				.map(employee -> modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
		logger.debug("list of employee details: " + employees);
		logger.info("Fetched all employee details");
		return employees;
	}

	/**
	 * Fetching details for logged in employee
	 * 
	 * @param String email
	 * @return EmployeeDTO
	 * @exception EmployeeNotFoundException
	 */ 
	@Override
	@PreAuthorize("# email == authentication.principal")
	public EmployeeDTO getEmployee(String email) {
		Employee employee = employeeRepository.findByEmail(email)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee with email: " + email + " not found"));
		logger.debug("logged in employee details: " + employee);
		logger.info("Fetched details for logged in employee");
		return modelMapper.map(employee, EmployeeDTO.class);
	}

	/**
	 * Updating details of logged in email
	 * 
	 * @param String      email
	 * @param EmployeeDTO employeeDTO
	 * @result String
	 * @exception EmployeeNotFoundException
	 * @exception ConstraintViolationException
	 */
	@Override
	public String updateEmployeeDetails(String email, EmployeeDTO employeeDTO) {
		Employee employee = employeeRepository.findByEmail(email)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee with email: " + email + " not found"));
		employee.setEmployeeDepartment(employeeDTO.getEmployeeDepartment());
		employee.setEmployeeName(employeeDTO.getEmployeeName());
		try {
			Employee updatedEmployee = employeeRepository.save(employee);
			logger.debug("updated logged in employee details :" + updatedEmployee);
			logger.info("Employee details are updated");
		} catch (Exception e) {
			throw new ConstraintViolationException("check the constraints for inputs");
		}
		return "Employee details are updated";
	}

	/**
	 * Deleting employee details by employeeId
	 * 
	 * @param long employeeId
	 * @return String
	 * @exception EmployeeNotFoundException
	 */
	@Override
	public String deleteEmployee(long employeeId) {
		employeeRepository.findById(employeeId).orElseThrow(
				() -> new EmployeeNotFoundException("No employee found with id: " + employeeId + " to delete"));
		employeeRepository.deleteById(employeeId);
		logger.info("employee details are deleted");
		return "employee details are deleted";
	}
}

package com.example.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.dto.ManagerDTO;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Manager;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.ManagerNotFoundException;
import com.example.employee.repository.ManagerRepository;

@Service
public class ManagerServiceImpl implements ManagerService {

	private static Logger logger = LoggerFactory.getLogger(ManagerServiceImpl.class);

	@Autowired
	private ManagerRepository managerRepository;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * creating new manager
	 * 
	 * @param ManagerDTO managerDto
	 * @return String
	 * @exception ConstraintViolationException
	 */
	@Override
	public String addManager(ManagerDTO managerDto) {

		Manager manager = modelMapper.map(managerDto, Manager.class);
		try {
			Manager savedManager = managerRepository.save(manager);
			logger.debug("saved manager details: " + savedManager);
		} catch (Exception e) {
			throw new ConstraintViolationException(
					"manager name should not blank and email must be unique and proper format");
		}
		logger.info("created a new manager");
		return "Manager added successfully";
	}

	/**
	 * Fetching details of all managers
	 * 
	 * @return List<ManagerDTO>
	 */
	@Override
	public List<ManagerDTO> getAllManagers() {
		List<ManagerDTO> managers = managerRepository.findAll().stream()
				.map(manager -> modelMapper.map(manager, ManagerDTO.class)).collect(Collectors.toList());
		logger.debug("list of manager details: " + managers);
		logger.info("Fetched details of all managers");
		return managers;
	}

	/**
	 * Fetching manager details by managerId
	 * 
	 * @param long managerId
	 * @return ManagerDTO
	 * @exception ManagerNotFoundException
	 */
	@Override
	public ManagerDTO getManager(long managerId) {
		Manager manager = managerRepository.findById(managerId)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + managerId + " not found"));
		logger.debug("manger details by id: " + manager);
		logger.info("Fetched manager details by managerId");
		return modelMapper.map(manager, ManagerDTO.class);
	}

	/**
	 * Updating manager details by managerId
	 * 
	 * @param long       managerId
	 * @param ManagerDTO managerId
	 * @return String
	 * @exception ManagerNotFoundException
	 */
	@Override
	public String updateManagerDetails(long managerId, ManagerDTO managerDTO) {
		Manager manager = managerRepository.findById(managerId)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + managerId + " not found"));
		manager.setEmail(managerDTO.getEmail());
		manager.setManagerDepartment(managerDTO.getManagerDepartment());
		manager.setManagerName(managerDTO.getManagerName());
		try {
			Manager updatedManager = managerRepository.save(manager);
			logger.debug("updated manager details: " + updatedManager);
		} catch (Exception e) {
			throw new ConstraintViolationException(
					"manager name should not blank and email must be unique and proper format");
		}
		logger.info("Updated the manager details by managerId");
		return "Manager details updated";
	}

	/**
	 * Deleting manager details by managerId
	 * 
	 * @param long managerId
	 * @return String
	 * @exception ManagerNotFoundException
	 */
	@Override
	public String deleteManagerById(long managerId) {
		managerRepository.findById(managerId).orElseThrow(
				() -> new ManagerNotFoundException("No manager found with id: " + managerId + " to delete"));
		managerRepository.deleteById(managerId);
		logger.info("Manager details deleted");
		return "Manager details deleted";
	}

	/**
	 * Fetching reporting employees
	 * 
	 * @param String email
	 * @return List<EmployeeDTO>
	 * @exception ManagerNotFoundException
	 */
	@Override
	public List<EmployeeDTO> getReportingEployees(String email) {
		Manager manager = managerRepository.findByEmail(email)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + email + " not found"));
		List<EmployeeDTO> reportingEmployees = manager.getReportingEmployees().stream()
				.map(employee -> modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
		logger.debug("fetched list of reporting employees: " + reportingEmployees);
		logger.info("fetched all reporting employees");
		return reportingEmployees;
	}

	/**
	 * adding reporting employees to the manager
	 * 
	 * @param String      email
	 * @param EmployeeDTO employee
	 * @return String
	 * @exception ManagerNotFoundException
	 */
	@Override
	public String addReportingEmployees(String email, EmployeeDTO employee) {
		Manager manager = managerRepository.findByEmail(email)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + email + " not found"));
		List<Employee> employeeList = manager.getReportingEmployees();
		employeeList.add(modelMapper.map(employee, Employee.class));
		manager.setReportingEmployees(employeeList);
		managerRepository.save(manager);
		logger.info("added employee to the manager");
		return "Reporting employee added to manager";
	}

	/**
	 * Fetching details of loggedIn manager
	 * 
	 * @param String email
	 * @return ManagerDTO
	 * @exception ManagerNotFoundException
	 */
	@Override
	public ManagerDTO getLoggedInManager(String email) {
		Manager manager = managerRepository.findByEmail(email)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + email + " not found"));
		ManagerDTO managerDto = modelMapper.map(manager, ManagerDTO.class);
		logger.debug("logged in manager details: " + manager);
		logger.info("fetched logged in manager details");
		return managerDto;
	}

	/**
	 * updating the details of logged in manager
	 * 
	 * @param ManagerDTO managerDto
	 * @return String
	 * @exception ManagerNotFoundException
	 */
	@Override
	public String updateLoggedInManager(String email, ManagerDTO managerDto) {
		Manager manager = managerRepository.findByEmail(email)
				.orElseThrow(() -> new ManagerNotFoundException("Manager with id: " + email + " not found"));
		manager.setManagerName(managerDto.getManagerName());
		manager.setManagerDepartment(managerDto.getManagerDepartment());
		try {
			Manager updatedManager = managerRepository.save(manager);
			logger.debug("updated manager details: " + updatedManager);
			logger.info("updated the details of logged in manager");
		} catch (Exception e) {
			throw new ConstraintViolationException(
					"manager name should not blank and email must be unique and proper format");
		}
		return "Manager details updated";
	}
}

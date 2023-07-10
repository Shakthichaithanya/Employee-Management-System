package com.example.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.dto.ManagerDTO;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Manager;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.ManagerNotFoundException;
import com.example.employee.repository.ManagerRepository;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private ManagerServiceImpl managerService;

	private ManagerDTO managerDTO;
	private EmployeeDTO employeeDTO;
	private Manager manager;
	private String email;
	private List<Employee> employees;

	@BeforeEach
	void setUp() throws Exception {
		employeeDTO = new EmployeeDTO(2, "prasad", "IT", "prasad@gmail.com");
		employees = new ArrayList<>();
		employees.add(new Employee(2, "prasad", "IT", "prasad@gmail.com"));
		managerDTO = new ManagerDTO();
		managerDTO.setManagerId(1);
		managerDTO.setEmail("siva@gmail.com");
		managerDTO.setManagerDepartment("account");
		managerDTO.setManagerName("siva");
		managerDTO.setReportingEmployees(employees);
		manager = new Manager();
		manager.setManagerId(1);
		manager.setEmail("siva@gmail.com");
		manager.setManagerDepartment("account");
		manager.setManagerName("siva");
		manager.setReportingEmployees(employees);

		email = "siva@gmail.com";
	}

	@Test
	void testAddManager() {
		BDDMockito.given(modelMapper.map(managerDTO, Manager.class)).willReturn(manager);
		BDDMockito.given(managerRepository.save(manager)).willReturn(manager);

		String message = managerService.addManager(managerDTO);
		assertThat(message).isEqualTo("Manager added successfully");
	}

	@Test
	void testAddManagerWithException() {
		BDDMockito.given(managerRepository.save(manager)).willThrow(ConstraintViolationException.class);

		ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
				() -> managerService.addManager(managerDTO));
		assertThat(exception.getMessage())
				.isEqualTo("manager name should not blank and email must be unique and proper format");
	}

	@Test
	void testGetAllManagers() {
		List<Manager> managers = new ArrayList<>();
		managers.add(manager);
		BDDMockito.given(managerRepository.findAll()).willReturn(managers);

		List<ManagerDTO> managersList = managerService.getAllManagers();

		assertThat(managersList).hasSize(1);
	}

	@Test
	void testGetManager() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.of(manager));
		BDDMockito.given(modelMapper.map(manager, ManagerDTO.class)).willReturn(managerDTO);
		ManagerDTO managerDto = managerService.getManager(manager.getManagerId());
		assertThat(managerDto).isNotNull();
	}

	@Test
	void testGetManagerWithException() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.getManager(manager.getManagerId()));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + manager.getManagerId() + " not found");
	}

	@Test
	void testUpdateManagerDetails() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.of(manager));
		BDDMockito.given(managerRepository.save(manager)).willReturn(manager);

		String message = managerService.updateManagerDetails(manager.getManagerId(), managerDTO);

		assertThat(message).isEqualTo("Manager details updated");
	}

	@Test
	void testUpdateManagerDetailsWithManagerNotFoundException() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.updateManagerDetails(manager.getManagerId(), managerDTO));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + manager.getManagerId() + " not found");
	}

	@Test
	void testUpdateManagerDetailsWithConstraintViolationException() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.of(manager));
		BDDMockito.given(managerRepository.save(manager)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> managerService.updateManagerDetails(manager.getManagerId(), managerDTO));

		assertThat(exception.getMessage())
				.isEqualTo("manager name should not blank and email must be unique and proper format");
	}

	@Test
	void testDeleteManagerById() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.of(manager));
		String message = managerService.deleteManagerById(manager.getManagerId());
		assertThat(message).isEqualTo("Manager details deleted");
	}

	@Test
	void testDeleteManagerByIdWithException() {
		BDDMockito.given(managerRepository.findById(manager.getManagerId())).willReturn(Optional.empty());
		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.deleteManagerById(manager.getManagerId()));

		assertThat(exception.getMessage())
				.isEqualTo("No manager found with id: " + manager.getManagerId() + " to delete");
	}

	@Test
	void testGetReportingEployees() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));
		List<EmployeeDTO> reportingEmployees = managerService.getReportingEployees(email);
		assertThat(reportingEmployees).hasSize(1);
	}

	@Test
	void testGetReportingEployeesWithException() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.getReportingEployees(email));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + email + " not found");
	}

	@Test
	void testAddReportingEmployees() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));

		String message = managerService.addReportingEmployees(email, employeeDTO);

		assertThat(message).isEqualTo("Reporting employee added to manager");
	}

	@Test
	void testAddReportingEmployeesWithException() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.addReportingEmployees(email, employeeDTO));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + email + " not found");

	}

	@Test
	void testGetLoggedInManager() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));
		BDDMockito.given(modelMapper.map(manager, ManagerDTO.class)).willReturn(managerDTO);
		ManagerDTO managerDetails = managerService.getLoggedInManager(email);
		assertThat(managerDetails).isNotNull();
	}

	@Test
	void testGetLoggedInManagerWithException() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.getLoggedInManager(email));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + email + " not found");
	}

	@Test
	void testUpdateLoggedInManager() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));
		BDDMockito.given(managerRepository.save(manager)).willReturn(manager);

		String message = managerService.updateLoggedInManager(email, managerDTO);
		assertThat(message).isEqualTo("Manager details updated");

	}

	@Test
	void testUpdateLoggedInManagerWithManagerNotFoundException() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.empty());

		Throwable exception = assertThrows(ManagerNotFoundException.class,
				() -> managerService.updateLoggedInManager(email, managerDTO));

		assertThat(exception.getMessage()).isEqualTo("Manager with id: " + email + " not found");

	}

	@Test
	void testUpdateLoggedInManagerWithConstraintViolationException() {
		BDDMockito.given(managerRepository.findByEmail(email)).willReturn(Optional.of(manager));
		BDDMockito.given(managerRepository.save(manager)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> managerService.updateLoggedInManager(email, managerDTO));

		assertThat(exception.getMessage())
				.isEqualTo("manager name should not blank and email must be unique and proper format");

	}

}

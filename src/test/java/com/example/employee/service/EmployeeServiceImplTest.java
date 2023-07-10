package com.example.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.entities.Employee;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private EmployeeServiceImpl employeeService;

	private EmployeeDTO employeeDTO;
	private Employee employee;
	private String email;

	@BeforeEach
	void setUp() throws Exception {
		employeeDTO = new EmployeeDTO();
		employeeDTO.setEmployeeid(1);
		employeeDTO.setEmail("siva@gmail.com");
		employeeDTO.setEmployeeDepartment("account");
		employeeDTO.setEmployeeName("siva");
		employee = new Employee();
		employee.setEmployeeId(1);
		employee.setEmail("siva@gmail.com");
		employee.setEmployeeDepartment("account");
		employee.setEmployeeName("siva");
		email = "siva2@gmail.com";

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testAddEmployee() {
		BDDMockito.given(employeeRepository.save(employee)).willReturn(employee);
		BDDMockito.given(modelMapper.map(employeeDTO, Employee.class)).willReturn(employee);

		String message = employeeService.addEmployee(employeeDTO);

		assertThat(message).isEqualTo("Employee added successfully");
	}

	@Test
	void testAddEmployeeWithException() {
		BDDMockito.given(employeeRepository.save(employee)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> employeeService.addEmployee(employeeDTO));

		assertThat(exception.getMessage()).isEqualTo("could not execute the statement");
	}

	@Test
	void testGetAllEmployees() {
		List<Employee> employeeList = new ArrayList<>();
		employeeList.add(employee);

		BDDMockito.given(employeeRepository.findAll()).willReturn(employeeList);
		List<EmployeeDTO> employees = employeeService.getAllEmployees();
		assertThat(employees).hasSize(1);
	}

	@Test
	void testGetEmployee() {
		BDDMockito.given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
		BDDMockito.given(modelMapper.map(employee, EmployeeDTO.class)).willReturn(employeeDTO);

		EmployeeDTO employee = employeeService.getEmployee(email);

		assertThat(employee).isEqualTo(employeeDTO);
	}

	@Test
	void testGetEmployeeWithException() {
		BDDMockito.given(employeeRepository.findByEmail(email)).willReturn(Optional.empty());

		Throwable exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployee(email));

		assertThat(exception.getMessage()).isEqualTo("Employee with email: " + email + " not found");
	}

	@Test
	void testUpdateEmployeeDetails() {
		BDDMockito.given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
		BDDMockito.given(employeeRepository.save(employee)).willReturn(employee);

		String message = employeeService.updateEmployeeDetails(email, employeeDTO);

		assertThat(message).isEqualTo("Employee details are updated");
	}

	@Test
	void testUpdateEmployeeDetailsWithEmployeeNotFoundException() {
		BDDMockito.given(employeeRepository.findByEmail(email)).willReturn(Optional.empty());
		Throwable exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.updateEmployeeDetails(email, employeeDTO));

		assertThat(exception.getMessage()).isEqualTo("Employee with email: " + email + " not found");
	}

	@Test
	void testUpdateEmployeeDetailsWithConstraintViolationException() {
		BDDMockito.given(employeeRepository.findByEmail(email)).willReturn(Optional.of(employee));
		BDDMockito.given(employeeRepository.save(employee)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> employeeService.updateEmployeeDetails(email, employeeDTO));

		assertThat(exception.getMessage()).isEqualTo("check the constraints for inputs");
	}

	@Test
	void testDeleteEmployee() {
		BDDMockito.given(employeeRepository.findById(employee.getEmployeeId())).willReturn(Optional.of(employee));
		String message = employeeService.deleteEmployee(employee.getEmployeeId());
		assertThat(message).isEqualTo("employee details are deleted");
	}

	@Test
	void testDeleteEmployeeWithException() {
		BDDMockito.given(employeeRepository.findById(employee.getEmployeeId())).willReturn(Optional.empty());
		Throwable exception = assertThrows(EmployeeNotFoundException.class,
				() -> employeeService.deleteEmployee(employee.getEmployeeId()));
		assertThat(exception.getMessage())
				.isEqualTo("No employee found with id: " + employee.getEmployeeId() + " to delete");
	}

}

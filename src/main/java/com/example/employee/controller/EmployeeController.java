package com.example.employee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.info.ResponseInfo;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.UserService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

	private final EmployeeService employeeService;
	private final UserService userService;

	@Autowired
	public EmployeeController(EmployeeService employeeService, UserService userService){
		this.employeeService = employeeService;
		this.userService = userService;
	}

	/**
	 * creating new Employee
	 * 
	 * @param EmployeeDTO employeeDto
	 * @return ResponseInfo
	 */
	@PostMapping("/employee/add")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> addEmployuee(@RequestBody EmployeeDTO employeeDto) {
		String message = employeeService.addEmployee(employeeDto);
		UserDTO user = new UserDTO();
		user.setEmail(employeeDto.getEmail());
		user.setPassword("abcd");
		user.setRole("Employee");
		userService.addUser(user);
		logger.info(message);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * fetching all Employee Details from the database
	 * 
	 * @return List<EmployeeDTO>
	 */
	@GetMapping()
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
		List<EmployeeDTO> employeesList = employeeService.getAllEmployees();
		logger.info("All employee details are fetched");
		return new ResponseEntity<>(employeesList, HttpStatus.OK);
	}

	/**
	 * Fetching details of the logged in employee
	 * 
	 * @return EmployeeDTO
	 */
	@GetMapping("/employee")
	@PreAuthorize("hasAuthority('Employee')")
	public ResponseEntity<EmployeeDTO> getLoggedInEmployeeDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getPrincipal().toString();
		System.out.println(email);
		EmployeeDTO employee = employeeService.getEmployee(email);
		logger.info("fetching employee details by email");
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}

	/**
	 * Updating details of the logged in employee
	 * 
	 * @param EmployeeDTO employee
	 * @return ResponseInfo
	 */
	@PutMapping("/employee")
	@PreAuthorize("hasAuthority('Employee')")
	public ResponseEntity<ResponseInfo> updateLoggedInEmployeeDetails(@RequestBody EmployeeDTO employee) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		String message = employeeService.updateEmployeeDetails(email, employee);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * updating employee details using email
	 * 
	 * @param  String      email
	 * @param EmployeeDTO employeeDto
	 * @return ResponseInfo
	 */
	@PutMapping("/employee/{email}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> updateEmployee(@PathVariable("email") String email,
			@RequestBody EmployeeDTO employeeDto) {

		String message = employeeService.updateEmployeeDetails(email, employeeDto);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * Deleting employee details using employeeId
	 * 
	 * @param long employeeId
	 * @return Void
	 */
	@DeleteMapping("/employee/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> deleteEmployeeById(@PathVariable("id") long employeeId) {
		String message = employeeService.deleteEmployee(employeeId);
		logger.info(message);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}

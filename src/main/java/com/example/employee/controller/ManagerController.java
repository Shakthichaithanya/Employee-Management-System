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
import com.example.employee.dto.ManagerDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.info.ResponseInfo;
import com.example.employee.service.ManagerService;
import com.example.employee.service.UserService;

@RestController
@RequestMapping("/managers")
public class ManagerController {

	private static Logger logger = LoggerFactory.getLogger(ManagerController.class);
	private final ManagerService managerService;
	private final UserService userService;

	@Autowired
	public ManagerController(ManagerService managerService, UserService userService) {
		this.managerService = managerService;
		this.userService = userService;
	}

	/**
	 * Creating new Manager
	 * 
	 * @param ManagerDTO managerDto
	 * @return ResponseInfo
	 */
	@PostMapping("/manager/add")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> addManager(@RequestBody ManagerDTO managerDto) {
		String message = managerService.addManager(managerDto);
		UserDTO user = new UserDTO();
		user.setEmail(managerDto.getEmail());
		user.setPassword("abcd");
		user.setRole("Manager");
		userService.addUser(user);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * Fetching all manager details from database
	 * 
	 * @return List<ManagerDTO>
	 */
	@GetMapping()
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<List<ManagerDTO>> getAllManagers() {
		List<ManagerDTO> managersList = managerService.getAllManagers();
		logger.info("All manager details are fetched");
		return new ResponseEntity<>(managersList, HttpStatus.OK);
	}

	/**
	 * Fetching manager details by managerId
	 * 
	 * @param long managerId
	 * @return ManagerDTO
	 */
	@GetMapping("/manager/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ManagerDTO> getManagerById(@PathVariable("id") long managerId) {
		ManagerDTO manager = managerService.getManager(managerId);
		logger.info("manager details are fetched by id");
		return new ResponseEntity<>(manager, HttpStatus.OK);
	}

	/**
	 * Fetching all reporting employees for the logged in manager
	 * 
	 * @return list<EmployeeDTO>
	 */
	@GetMapping("/manager/employees")
	@PreAuthorize("hasAuthority('Manager')")
	public ResponseEntity<List<EmployeeDTO>> getReportingEmployees() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		List<EmployeeDTO> reportingEmployees = managerService.getReportingEployees(email);
		logger.info("fetched all reporting employees for the logged in manager");
		return new ResponseEntity<>(reportingEmployees, HttpStatus.OK);
	}

	/**
	 * Fetching details of logged in manager
	 * 
	 * @return ManagerDTO
	 */
	@GetMapping("/manager")
	@PreAuthorize("hasAuthority('Manager')")
	public ResponseEntity<ManagerDTO> getLoggedInManagerDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		ManagerDTO manager = managerService.getLoggedInManager(email);
		logger.info("fetched the details of logged in manager");
		return new ResponseEntity<>(manager, HttpStatus.OK);
	}

	/**
	 * Updating details of the logged in manager
	 * 
	 * @param ManagerDTO managerDto
	 * @return ResponseInfo
	 */
	@PutMapping("/manager")
	@PreAuthorize("hasAuthority('Manager')")
	public ResponseEntity<ResponseInfo> updateLoggedInManagerDetails(@RequestBody ManagerDTO managerDto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		String message = managerService.updateLoggedInManager(email, managerDto);
		ResponseInfo responsInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responsInfo, HttpStatus.CREATED);
	}

	/**
	 * add reporting employees to the manager
	 * 
	 * @param String      email
	 * @param EmployeeDTO employee
	 * @return ResponseInfo
	 */
	@PutMapping("/manager/employees/{email}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> addReportingEmployees(@PathVariable("email") String email,
			@RequestBody EmployeeDTO employee) {
		String message = managerService.addReportingEmployees(email, employee);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * Updating manager details by managerId
	 * 
	 * @param long managerId
	 * @return ResponseInfo
	 */
	@PutMapping("/manager/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> updateManagerDetailsById(@PathVariable("id") long managerId,
			@RequestBody ManagerDTO manager) {
		String message = managerService.updateManagerDetails(managerId, manager);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * Deleting manager details using mangerId
	 * 
	 * @param long managerId
	 * @return Void
	 */
	@DeleteMapping("/manager/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> deleteManagerById(@PathVariable("id") long managerId) {
		String message = managerService.deleteManagerById(managerId);
		logger.info(message);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}

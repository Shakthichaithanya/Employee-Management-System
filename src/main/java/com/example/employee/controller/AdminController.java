package com.example.employee.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.AdminDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.info.ResponseInfo;
import com.example.employee.service.AdminService;
import com.example.employee.service.UserService;

@RestController
@RequestMapping("/admins")
public class AdminController {

	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	private final AdminService adminService;
	private final UserService userService;

	@Autowired
	public AdminController(AdminService adminService, UserService userService){
		this.adminService = adminService;
		this.userService = userService;
	}

	/**
	 * creating a new Admin
	 * 
	 * @param AdminDTO adminDto
	 * @return ResponseInfo
	 */
	@PostMapping("/admin/add")
	public ResponseEntity<ResponseInfo> addAdmin(@RequestBody AdminDTO adminDto) {
		String message = adminService.addAdmin(adminDto);
		UserDTO user = new UserDTO();
		user.setEmail(adminDto.getEmail());
		user.setPassword("abcd");
		user.setRole("Admin");
		userService.addUser(user);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * fetching all admins from database
	 * 
	 * @return List<AdminDTO>
	 */
	@GetMapping()
	public ResponseEntity<List<AdminDTO>> getAllAdmins() {
		List<AdminDTO> admins = adminService.getAllAdmins();
		logger.info("all admin details are fetched");
		return new ResponseEntity<>(admins, HttpStatus.OK);
	}

	/**
	 * fetching details of admin by adminId
	 * 
	 * @param long adminId
	 * @return AdminDTO
	 */
	@GetMapping("/admin/{id}")
	public ResponseEntity<AdminDTO> getAdminById(@PathVariable("id") long adminId) {
		AdminDTO admin = adminService.getAdminById(adminId);
		logger.info("fetched admin details by id");
		return new ResponseEntity<>(admin, HttpStatus.OK);
	}

	/**
	 * updating admin details
	 * 
	 * @param long     adminId
	 * @param AdminDTO adminDto
	 * @return RensponseInfo
	 */
	@PutMapping("/admin/{id}")
	public ResponseEntity<ResponseInfo> updateAdminDetails(@PathVariable("id") long adminId,
			@RequestBody AdminDTO adminDto) {
		String message = adminService.updateAdminDetails(adminId, adminDto);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * deleting admin details
	 * 
	 * @param long adminId
	 * @return
	 * @return ResponseInfo
	 */
	@DeleteMapping("/admin/{id}")
	public ResponseEntity<Void> deleteAdminById(@PathVariable("id") long adminId) {
		String email = adminService.getAdminById(adminId).getEmail();
		String message = adminService.deleteAdminById(adminId);
		userService.deleteUserByEmail(email);
		logger.info(message);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
}

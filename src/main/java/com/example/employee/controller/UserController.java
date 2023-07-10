package com.example.employee.controller;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.dto.LoginDTO;
import com.example.employee.dto.PasswordDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.filter.JwtService;
import com.example.employee.info.ResponseInfo;
import com.example.employee.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Creating new user
	 * 
	 * @param UserDTO userDto
	 * @return ResponseInfo
	 */
	@PostMapping("/user/add")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<ResponseInfo> addUser(@RequestBody UserDTO userDto) {
		String message = userService.addUser(userDto);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		logger.info(message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * fetching all user details by email
	 * 
	 * @param String email
	 * @return UserDTO
	 */
	@GetMapping("/user/{email}")
	public ResponseEntity<UserDTO> getUserDetailsByEmail(@PathVariable("email") String email) {
		UserDTO user = modelMapper.map(userService.getUserByEmail(email), UserDTO.class);
		logger.info("fetched all user details by email");
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * authenticating the login user
	 * 
	 * @param LoginDTO loginUser
	 * @return String
	 */
	@PostMapping("/login")
	public String authenticateUser(@RequestBody LoginDTO loginUser) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword()));
		if (authentication.isAuthenticated()) {
			logger.info("JWT token generated");
			return jwtService.generateJwtToken(userService.getUserByEmail(loginUser.getEmail()));

		}
		throw new BadCredentialsException("Bad credentials");

	}

	/**
	 * allowing login users to change their password
	 * 
	 * @param PasswordDTO password
	 * @return String
	 */
	@PatchMapping("/user/changepassword")
	public ResponseEntity<ResponseInfo> userPasswordChange(@RequestBody PasswordDTO password) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		String message = userService.changePassword(email, password);
		logger.info(message);
		ResponseInfo responseInfo = new ResponseInfo(HttpStatus.CREATED, message);
		return new ResponseEntity<>(responseInfo, HttpStatus.CREATED);
	}

	/**
	 * deleting users with userId
	 * 
	 * @param userId
	 * @return void
	 */
	@DeleteMapping("/user/{id}")
	@PreAuthorize("hasAuthority('Admin')")
	public ResponseEntity<Void> deleteUserById(@PathVariable("id") long userId) {
		String message = userService.deleteUser(userId);
		logger.info(message);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}

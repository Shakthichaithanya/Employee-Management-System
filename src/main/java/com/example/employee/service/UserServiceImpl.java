package com.example.employee.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.employee.dto.PasswordDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.entities.Users;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.UserNotFoundException;
import com.example.employee.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * creating new user
	 * 
	 * @param UserDTO userDto
	 * @return String
	 */
	@Override
	public String addUser(UserDTO userDto) {

		Users user = new Users();
		user.setEmail(userDto.getEmail());
		user.setRole(userDto.getRole());
		final String password = passwordEncoder.encode(userDto.getPassword());
		user.setPassword(password);
		try {
			Users savedUser = userRepository.save(user);
			logger.debug("saved user details: " + savedUser);
		} catch (Exception e) {
			throw new ConstraintViolationException("Could not execute the statement");
		}
		logger.info("user added");
		return "User added";
	}

	/**
	 * Fetching user details by email
	 * 
	 * @param String email
	 * @return UserDTO
	 * @exception UserNotFoundException
	 */
	@Override
	public Users getUserByEmail(String email) {
		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("user not registered"));
		logger.debug("user details by email: " + user);
		logger.info("fetched user details by email");
		return user;
	}

	/**
	 * allowing logged in user to change password
	 * 
	 * @param String      email
	 * @param PasswordDTO password
	 * @return String
	 * @exception UserNotFoundException
	 */
	@Override
	public String changePassword(String email, PasswordDTO password) {

		Users user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("user not registered"));
		final String encryptedPassword = passwordEncoder.encode(password.getPassword());
		user.setPassword(encryptedPassword);
		try {
			Users updatedUser = userRepository.save(user);
			logger.debug("updated password details " + updatedUser);
			logger.info("password updated");
		} catch (Exception e) {
			throw new ConstraintViolationException("check password");
		}
		return "password changed successfully";
	}

	@Override
	public String deleteUser(long userId) {
		userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
		userRepository.deleteById(userId);
		logger.info("user deleted");
		return "user deleted";
	}

}

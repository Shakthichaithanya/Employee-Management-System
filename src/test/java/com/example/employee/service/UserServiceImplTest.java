package com.example.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.employee.dto.PasswordDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.entities.Users;
import com.example.employee.exception.ConstraintViolationException;
import com.example.employee.exception.UserNotFoundException;
import com.example.employee.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private UserServiceImpl userService;

	private UserDTO userDTO;
	private Users user;
	private String password;
	private String email;
	private PasswordDTO passwordDTO;

	@BeforeEach
	void setUp() throws Exception {

		email = "shakthits@gmail.com";
		userDTO = new UserDTO(1, "shakthits@gmail.com", "abcd", "Admin");
		password = passwordEncoder.encode(userDTO.getPassword());
		user = new Users(1, "shakthits@gmail.com", password, "Admin");
		passwordDTO = new PasswordDTO(userDTO.getPassword());
	}

	@Test
	void testAddUserWithConstraintVoilationException() {
		BDDMockito.given(passwordEncoder.encode("abcd")).willReturn(password);
		BDDMockito.given(userRepository.save(user)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class, () -> userService.addUser(userDTO));

		assertThat(exception.getMessage()).isEqualTo("Could not execute the statement");
	}

	@Test
	void testAddUser() {
		BDDMockito.given(passwordEncoder.encode("abcd")).willReturn(password);
		Users newUser = new Users();
		newUser.setEmail(userDTO.getEmail());
		newUser.setRole(userDTO.getRole());
		BDDMockito.given(userRepository.save(newUser)).willReturn(user);
		String message = userService.addUser(userDTO);
		assertThat(message).isEqualTo("User added");
	}

	@Test
	void testGetUserByEmail() {
		BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
		Users userDetails = userService.getUserByEmail(email);

		assertThat(userDetails).isNotNull();
	}

	@Test
	void testGetUserByEmailWithException() {
		BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.empty());
		Throwable exception = assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
		assertThat(exception.getMessage()).isEqualTo("user not registered");

	}

	@Test
	void testChangePassword() {
		BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
		BDDMockito.given(passwordEncoder.encode(userDTO.getPassword())).willReturn(password);
		BDDMockito.given(userRepository.save(user)).willReturn(user);

		String message = userService.changePassword(email, passwordDTO);

		assertThat(message).isEqualTo("password changed successfully");

	}

	@Test
	void testChangePasswordWithException() {
		BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.empty());
		Throwable exception = assertThrows(UserNotFoundException.class,
				() -> userService.changePassword(email, passwordDTO));
		assertThat(exception.getMessage()).isEqualTo("user not registered");
	}

	@Test
	void testChangePasswordWithConstraintVoilationException() {
		BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
		BDDMockito.given(passwordEncoder.encode(userDTO.getPassword())).willReturn(password);
		BDDMockito.given(userRepository.save(user)).willThrow(ConstraintViolationException.class);

		Throwable exception = assertThrows(ConstraintViolationException.class,
				() -> userService.changePassword(email, passwordDTO));
		assertThat(exception.getMessage()).isEqualTo("check password");

	}

	@Test
	void testDeleteUser() {
		BDDMockito.given(userRepository.findById(user.getUserID())).willReturn(Optional.of(user));

		String message = userService.deleteUser(user.getUserID());

		assertThat(message).isEqualTo("user deleted");
	}

	@Test
	void testDeleteUserWithException() {
		BDDMockito.given(userRepository.findById(user.getUserID())).willReturn(Optional.empty());

		Throwable exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(user.getUserID()));
		assertThat(exception.getMessage()).isEqualTo("user not found");

	}

}

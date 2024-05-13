package com.example.employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.employee.dto.LoginDTO;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.employee.dto.PasswordDTO;
import com.example.employee.dto.UserDTO;
import com.example.employee.entities.Users;
import com.example.employee.filter.JwtService;
import com.example.employee.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@MockBean
	private UserService userService;
	@MockBean
	private JwtService jwtService;
	@MockBean
	private AuthenticationManager authenticationManager;
	@MockBean
	private Authentication authentication;
	@MockBean
	private ModelMapper modelMapper;
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private Users user;
	private UserDTO userDTO;
	private String email;
	private PasswordDTO passwordDTO;

	@Autowired
	UserController userController;

	@BeforeEach
	void setUp() throws Exception {
		user = new Users(1, "shakthi@gmail.com", "abcd", "Admin");
		userDTO = new UserDTO(1, "shakthi@gmail.com", "abcd", "Admin");
		email = "shakthi@gmail.com";
		passwordDTO = new PasswordDTO("abcd");
	}

	@Test
	void testAddUser() throws Exception {
		BDDMockito.given(userService.addUser(userDTO)).willReturn("User added");

		ResultActions response = mockMvc.perform(post("/users/user/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userDTO)));

		response.andExpect(status().isCreated()).andExpect(jsonPath("$.message", CoreMatchers.is("User added")));
	}

	@Test
	void testAuthenticateUser_Success() throws Exception {
		// Mocking user and login DTO
		Users mockedUser = new Users();
		mockedUser.setEmail("test@example.com");

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setEmail("test@example.com");
		loginDTO.setPassword("password123");

		// Mock authentication result
		when(authenticationManager.authenticate(any()))
				.thenReturn(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

		// Mock user retrieval
		when(userService.getUserByEmail(loginDTO.getEmail())).thenReturn(mockedUser);

		// Mock JWT token generation
		when(jwtService.generateJwtToken(mockedUser)).thenReturn("mocked_jwt_token");

		// Call the method
//		String jwtToken = .authenticateUser(loginDTO);

		String jwtToken = userController.authenticateUser(loginDTO);
		ResultActions response = mockMvc.perform(post("/users/login")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(loginDTO)));

		response.andExpect(status().isUnauthorized());
		// Verify
		assertNotNull(jwtToken);
		assertEquals("mocked_jwt_token", jwtToken);
	}

	@Test
	void testGetUserDetailsByEmail() throws Exception {
		BDDMockito.given(userService.getUserByEmail(email)).willReturn(user);
		BDDMockito.given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);

		ResultActions response = mockMvc.perform(get("/users/user/" + email));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.userId", CoreMatchers.is(1)));
	}

	@Test
	@WithMockUser(username = "shakthi@gmail.com", authorities = "Admin")
	void testUserPasswordChange() throws Exception {
		BDDMockito.given(userService.changePassword(email, passwordDTO)).willReturn("password changed successfully");

		ResultActions response = mockMvc.perform(patch("/users/user/changepassword")
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(passwordDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("password changed successfully")));
	}

	@Test
	void testDeleteUserById() throws Exception {
		BDDMockito.given(userService.deleteUser(user.getUserID())).willReturn("user deleted");

		ResultActions response = mockMvc.perform(delete("/users/user/" + user.getUserID()));

		response.andExpect(status().isNoContent());
	}

}

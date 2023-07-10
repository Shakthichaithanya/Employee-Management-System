package com.example.employee.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.employee.dto.AdminDTO;
import com.example.employee.entities.Admin;
import com.example.employee.filter.JwtService;
import com.example.employee.service.AdminService;
import com.example.employee.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

	@MockBean
	private AdminService adminService;
	@MockBean
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private JwtService jwtService;

	@Autowired
	private MockMvc mockMvc;

	private Admin admin;
	private AdminDTO adminDTO;

	@BeforeEach
	void setUp() throws Exception {
		admin = new Admin(1L, "shakthi", "IT", "shakthi@gmail.com");
		adminDTO = new AdminDTO(1, "shakthi", "IT", "shakthi@gmail.com");

	}

	@Test
	void testAddAdmin() throws JsonProcessingException, Exception {

		BDDMockito.given(adminService.addAdmin(adminDTO)).willReturn("admin added");

		ResultActions response = mockMvc.perform(post("/admins/admin/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(adminDTO)));

		response.andExpect(status().isCreated()).andExpect(jsonPath("$.message", CoreMatchers.is("admin added")));
	}

	@Test
	void testGetAllAdmins() throws JsonProcessingException, Exception {
		List<AdminDTO> admins = new ArrayList<>();
		admins.add(adminDTO);
		BDDMockito.given(adminService.getAllAdmins()).willReturn(admins);

		ResultActions response = mockMvc.perform(get("/admins"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", CoreMatchers.is(admins.size())));
	}

	@Test
	void testGetAdminById() throws Exception {
		BDDMockito.given(adminService.getAdminById(adminDTO.getAdminId())).willReturn(adminDTO);
		ResultActions response = mockMvc.perform(get("/admins/admin/" + adminDTO.getAdminId()));
		response.andExpect(status().isOk()).andExpect(jsonPath("$.adminId", CoreMatchers.is(1)));
	}

	@Test
	void testUpdateAdminDetails() throws JsonProcessingException, Exception {
		BDDMockito.given(adminService.updateAdminDetails(admin.getAdminId(), adminDTO))
				.willReturn("admin details updated");
		ResultActions response = mockMvc.perform(put("/admins/admin/" + admin.getAdminId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(adminDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("admin details updated")));
	}

	@Test
	void testDeleteAdminById() throws Exception {
		BDDMockito.given(adminService.deleteAdminById(admin.getAdminId())).willReturn("Admin details deleted");

		ResultActions response = mockMvc.perform(delete("/admins/admin/" + admin.getAdminId()));

		response.andExpect(status().isNoContent());
	}

}

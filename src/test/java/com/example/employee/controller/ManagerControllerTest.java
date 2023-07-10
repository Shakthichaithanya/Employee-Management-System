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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.employee.dto.EmployeeDTO;
import com.example.employee.dto.ManagerDTO;
import com.example.employee.entities.Employee;
import com.example.employee.entities.Manager;
import com.example.employee.filter.JwtService;
import com.example.employee.service.ManagerService;
import com.example.employee.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ManagerController.class)
@AutoConfigureMockMvc(addFilters = false)
class ManagerControllerTest {

	@MockBean
	private ManagerService managerService;
	@MockBean
	private JwtService jwtService;
	@MockBean
	private UserService userService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	private Manager manager;
	private ManagerDTO managerDTO;
	private Employee employee;
	private String email;
	private EmployeeDTO employeeDTO;

	@BeforeEach
	void setUp() throws Exception {
		employee = new Employee(1, "siva", "IT", "siva@gmail.com");
		employeeDTO = new EmployeeDTO(1, "siva", "IT", "siva@gmail.com");
		List<Employee> employees = new ArrayList<>();
		employees.add(employee);
		manager = new Manager(1, "shakthi", "IT", "shakthi@gmail.com", employees);
		managerDTO = new ManagerDTO(1, "shakthi", "IT", "shakthi@gmail.com", employees);
		email = "shakthi@gmail.com";
	}

	@Test
	void testAddManager() throws JsonProcessingException, Exception {
		BDDMockito.given(managerService.addManager(managerDTO)).willReturn("Manager added successfully");

		ResultActions response = mockMvc.perform(post("/managers/manager/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(managerDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Manager added successfully")));
	}

	@Test
	void testGetAllManagers() throws Exception {
		List<ManagerDTO> managers = new ArrayList<>();
		managers.add(managerDTO);
		BDDMockito.given(managerService.getAllManagers()).willReturn(managers);

		ResultActions response = mockMvc.perform(get("/managers"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", CoreMatchers.is(managers.size())));

	}

	@Test
	void testGetManagerById() throws Exception {
		BDDMockito.given(managerService.getManager(manager.getManagerId())).willReturn(managerDTO);

		ResultActions response = mockMvc.perform(get("/managers/manager/" + manager.getManagerId()));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.managerId", CoreMatchers.is(1)));
	}

	@Test
	@WithMockUser(username = "shakthi@gmail.com", authorities = "Manager")
	void testGetReportingEmployees() throws Exception {
		List<EmployeeDTO> employees = new ArrayList<>();
		employees.add(employeeDTO);

		BDDMockito.given(managerService.getReportingEployees(email)).willReturn(employees);

		ResultActions response = mockMvc.perform(get("/managers/manager/employees"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", CoreMatchers.is(1)));
		;
	}

	@Test
	@WithMockUser(username = "shakthi@gmail.com", authorities = "Manager")
	void testGetLoggedInManagerDetails() throws Exception {
		BDDMockito.given(managerService.getLoggedInManager(email)).willReturn(managerDTO);

		ResultActions response = mockMvc.perform(get("/managers/manager"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.managerId", CoreMatchers.is(1)));

	}

	@Test
	@WithMockUser(username = "shakthi@gmail.com", authorities = "Manager")
	void testUpdateLoggedInManagerDetails() throws JsonProcessingException, Exception {
		BDDMockito.given(managerService.updateLoggedInManager(email, managerDTO)).willReturn("Manager details updated");
		ResultActions response = mockMvc.perform(put("/managers/manager").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(managerDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Manager details updated")));
	}

	@Test
	void testAddReportingEmployees() throws JsonProcessingException, Exception {
		BDDMockito.given(managerService.addReportingEmployees(email, employeeDTO))
				.willReturn("Reporting employee added to manager");

		ResultActions response = mockMvc.perform(put("/managers/manager/employees/" + email)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Reporting employee added to manager")));
	}

	@Test
	void testUpdateManagerDetailsById() throws JsonProcessingException, Exception {

		BDDMockito.given(managerService.updateManagerDetails(manager.getManagerId(), managerDTO))
				.willReturn("Manager details updated");

		ResultActions response = mockMvc.perform(put("/managers/manager/" + manager.getManagerId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(managerDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Manager details updated")));
	}

	@Test
	void testDeleteManagerById() throws Exception {
		BDDMockito.given(managerService.deleteManagerById(manager.getManagerId()))
				.willReturn("Manager details deleted");

		ResultActions response = mockMvc.perform(delete("/managers/manager/" + manager.getManagerId()));

		response.andExpect(status().isNoContent());
	}

}

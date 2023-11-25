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
import org.junit.jupiter.api.DisplayName;
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
import com.example.employee.entities.Employee;
import com.example.employee.filter.JwtService;
import com.example.employee.service.EmployeeService;
import com.example.employee.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

	@MockBean
	private EmployeeService employeeService;
	@MockBean
	private UserService userService;

	@MockBean
	private JwtService jwtService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	private Employee employee;
	private EmployeeDTO employeeDTO;
	private String email;

	@BeforeEach
	void setUp() throws Exception {
		employee = new Employee(1, "siva", "Account", "siva@gmail.com");
		employeeDTO = new EmployeeDTO(1, "siva", "Account", "siva@gmail.com");
		email = employee.getEmail();
	}

	@Test
	@DisplayName("JUnit test for adding new Employee")
	void testAddEmployee() throws Exception {
		BDDMockito.given(employeeService.addEmployee(employeeDTO)).willReturn("Employee added successfully");
		ResultActions response = mockMvc.perform(post("/employees/employee/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeDTO)));
		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Employee added successfully")));
	}

	@Test
	@DisplayName("JUnit test for getting all employees")
	void testGetAllEmployees() throws Exception {
		List<EmployeeDTO> employees = new ArrayList<>();
		employees.add(employeeDTO);
		BDDMockito.given(employeeService.getAllEmployees()).willReturn(employees);

		ResultActions response = mockMvc.perform(get("/employees"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.size()", CoreMatchers.is(employees.size())));
	}

	@Test
	@WithMockUser(username = "siva@gmail.com", authorities = "Employee")
	@DisplayName("JUnit test for getting logged in employee details")
	void testGetLoggedInEmployeeDetails() throws Exception {

		BDDMockito.given(employeeService.getEmployee(email)).willReturn(employeeDTO);

		ResultActions response = mockMvc.perform(get("/employees/employee"));

		response.andExpect(status().isOk()).andExpect(jsonPath("$.employeeid", CoreMatchers.is(1)));
	}

	@Test
	@WithMockUser(username = "siva@gmail.com", authorities = "Employee")
	@DisplayName("JUnit test for updating logged in employee details")
	void testUpdateLoggedInEmployeeDetails() throws Exception {
		BDDMockito.given(employeeService.updateEmployeeDetails(email, employeeDTO))
				.willReturn("Employee details are updated");

		ResultActions response = mockMvc.perform(put("/employees/employee").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Employee details are updated")));
	}

	@Test
	@DisplayName("JUnit test for updating employee details")
	void testUpdateEmployee() throws Exception {
		BDDMockito.given(employeeService.updateEmployeeDetails(email, employeeDTO))
				.willReturn("Employee details are updated");
		ResultActions response = mockMvc.perform(put("/employees/employee/" + email)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(employeeDTO)));

		response.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message", CoreMatchers.is("Employee details are updated")));
	}

	@Test
	@DisplayName("JUnit test for deleting employees")
	void testDeleteEmployeeById() throws Exception {
		BDDMockito.given(employeeService.deleteEmployee(employee.getEmployeeId()))
				.willReturn("employee details are deleted");

		ResultActions response = mockMvc.perform(delete("/employees/employee/" + employee.getEmployeeId()));

		response.andExpect(status().isNoContent());
	}

}

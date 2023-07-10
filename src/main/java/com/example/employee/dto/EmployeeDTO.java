package com.example.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

	private long employeeid;
	private String employeeName;
	private String employeeDepartment;
	private String email;

}

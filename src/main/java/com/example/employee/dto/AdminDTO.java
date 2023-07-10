package com.example.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {

	private long adminId;
	private String adminName;
	private String adminDepartment;
	private String email;

}

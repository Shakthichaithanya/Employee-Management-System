package com.example.employee.info;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {

	private HttpStatus status;
	private Date date;
	private String message;
	private String description;
}

package com.example.employee.exception;

public class ManagerNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ManagerNotFoundException(String message) {
		super(message);
	}

}

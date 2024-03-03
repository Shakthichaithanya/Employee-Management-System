package com.example.employee.exception;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.employee.info.ErrorInfo;

@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(value = { ManagerNotFoundException.class })
	public ResponseEntity<ErrorInfo> managerNotFoundExceptionHandeler(ManagerNotFoundException exception,
			WebRequest request) {
		logger.error(exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.NOT_FOUND, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { EmployeeNotFoundException.class })
	public ResponseEntity<ErrorInfo> employeeNotFoundExceptionHandeler(EmployeeNotFoundException exception,
			WebRequest request) {
		logger.error(exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.NOT_FOUND, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { AdminNotFoundException.class })
	public ResponseEntity<ErrorInfo> adminNotFoundExceptionHandeler(AdminNotFoundException exception,
			WebRequest request) {
		logger.error(exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.NOT_FOUND, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	public ResponseEntity<ErrorInfo> acessDeniedExceptionHandeler(AccessDeniedException exception, WebRequest request) {
		logger.error("Access Denied Exceptions : " + exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.FORBIDDEN, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<ErrorInfo> constraintViolationExceptionHandeler(ConstraintViolationException exception,
			WebRequest request) {
		logger.error("Constraint Violation exception : " + exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(value = { BadCredentialsException.class })
	public ResponseEntity<ErrorInfo> badCredentialsExceptionHandeler(BadCredentialsException exception,
			WebRequest request) {
		logger.error("Bad Credential Exception: " + exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.UNAUTHORIZED, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = { UserNotFoundException.class })
	public ResponseEntity<ErrorInfo> userNotFoundExceptionHandeler(UserNotFoundException exception,
			WebRequest request) {
		logger.error(exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.UNAUTHORIZED, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = { TransactionSystemException.class })
	public ResponseEntity<ErrorInfo> transactionSystemExceptionHandeler(TransactionSystemException exception,
			WebRequest request) {
		logger.error(exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.BAD_REQUEST, new Date(),
				exception.getMostSpecificCause().getMessage(), request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<ErrorInfo> exceptionHandeler(Exception exception, WebRequest request) {
		logger.error("internal server error : " + exception.getMessage());
		ErrorInfo error = new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, new Date(), exception.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}

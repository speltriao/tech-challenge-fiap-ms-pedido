package com.galega.order.adapters.in.rest.handler;

import com.galega.order.adapters.in.rest.dto.ErrorDTO;
import com.galega.order.domain.enums.OrderStatusEnum;
import com.galega.order.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ExceptionHandlerControllerTest {

	private ExceptionHandlerController exceptionHandlerController;

	@BeforeEach
	public void setUp() {
		exceptionHandlerController = new ExceptionHandlerController();
	}

	@Test
	public void testHandleIllegalArgumentException() {
		IllegalArgumentException exception = new IllegalArgumentException("Invalid parameter");
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleIllegalArgumentException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleMethodArgumentNotValidException() {
		FieldError fieldError = new FieldError("objectName", "field", "Field is required");
		BindingResult bindingResult = mock(BindingResult.class);
		Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.Collections.singletonList(fieldError));

		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
		ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();

		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleMethodArgumentNotValidException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleOrderAlreadyWithStatusException() {
		OrderAlreadyWithStatusException exception = new OrderAlreadyWithStatusException(UUID.randomUUID(), OrderStatusEnum.CREATED);
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleOrderAlreadyWithStatus(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleEntityNotFoundException() {
		EntityNotFoundException exception = new EntityNotFoundException("Order not found", UUID.randomUUID());
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleOrderAlreadyWithStatus(exception);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testHandleMandatoryFieldException() {
		MandatoryFieldException exception = new MandatoryFieldException("Mandatory field missing");
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleMandatoryField(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());}

	@Test
	public void testHandleOrderNotReadyException() {
		OrderNotReadyException exception = new OrderNotReadyException("Order not ready");
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleOrderNotReady(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleInvalidCpfException() {
		InvalidCpfException exception = new InvalidCpfException();
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleInvalidCpf(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleEntityAlreadyExistException() {
		EntityAlreadyExistException exception = new EntityAlreadyExistException("Entity already exists");
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleEntityAlreadyExist(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleHttpMessageNotReadableException() {
		HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid message format");
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleHttpMessageNotReadable(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testHandleDuplicateKeyException() {
		DuplicateKeyException exception = new DuplicateKeyException("Duplicate key violation");

		ExceptionHandlerController exceptionHandlerController = new ExceptionHandlerController();
		ResponseEntity<ErrorDTO> response = exceptionHandlerController.handleDuplicateKeyException(exception);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

}
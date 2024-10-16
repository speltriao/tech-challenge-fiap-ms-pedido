package com.fiap.techchallenge.adapters.in.rest.handler;


import com.fiap.techchallenge.adapters.in.rest.dto.ErrorDTO;
import com.fiap.techchallenge.domain.exception.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorDTO errorResponse = new ErrorDTO("Parameters or request body is invalid", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(MethodArgumentNotValidException exception) {
        Locale.setDefault(Locale.US);
        FieldError field = exception.getBindingResult().getFieldErrors().getFirst();
        String details = "Field '" + field.getField() + "' " + field.getDefaultMessage();
        ErrorDTO errorResponse = new ErrorDTO("Parameters or request body is invalid", details);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(OrderAlreadyWithStatusException.class)
    public ResponseEntity<ErrorDTO> handleOrderAlreadyWithStatus(OrderAlreadyWithStatusException exception) {
        ErrorDTO errorResponse = new ErrorDTO(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleOrderAlreadyWithStatus(EntityNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MandatoryFieldException.class)
    public ResponseEntity<ErrorDTO> handleMandatoryField(MandatoryFieldException exception) {
        ErrorDTO errorResponse = new ErrorDTO(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(OrderNotReadyException.class)
    public ResponseEntity<ErrorDTO> handleOrderNotReady(OrderNotReadyException exception) {
        ErrorDTO errorResponse = new ErrorDTO(exception.getMessage(), exception.getDetails());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(InvalidCpfException.class)
    public ResponseEntity<ErrorDTO> handleInvalidCpf(InvalidCpfException exception) {
        ErrorDTO errorResponse = new ErrorDTO(exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ErrorDTO> handleEntityAlreadyExist(EntityAlreadyExistException exception) {
        ErrorDTO errorResponse = new ErrorDTO("Duplicated data found", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        ErrorDTO errorResponse = new ErrorDTO("Invalid body", exception.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorDTO> handleDuplicateKeyException(DuplicateKeyException exception) {
        ErrorDTO errorResponse = new ErrorDTO("Invalid body", exception.getCause().getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }


}

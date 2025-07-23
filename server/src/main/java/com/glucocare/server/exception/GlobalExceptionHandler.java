package com.glucocare.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ExceptionResponse> applicationExceptionHandling(ApplicationException exception) {
        var errorMessage = exception.getErrorMessage();
        return getExceptionResponse(errorMessage.getMessage(), HttpStatus.valueOf(errorMessage.getCode()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentNotValidExceptionHandling(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(fieldError.getDefaultMessage());
        }

        return getExceptionResponse(builder.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> internalServerExceptionHandling(Exception exception) {
        return getExceptionResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> getExceptionResponse(String message, HttpStatus status) {
        var response = new ExceptionResponse(status.value(), message);
        return ResponseEntity.status(status)
                             .body(response);
    }
}

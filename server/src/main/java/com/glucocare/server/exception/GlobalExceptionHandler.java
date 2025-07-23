package com.glucocare.server.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 리소스에 대한 접근입니다.";
    private static final String DUPLICATED_EMAIL_MESSAGE = "이미 존재하는 이메일입니다.";
    private static final String DUPLICATED_NAME_MESSAGE = "이미 존재하는 이름입니다.";
    private static final String INVALID_LOGIN_REQUEST_MESSAGE = "잘못된 이메일이거나 패스워드입니다.";
    private static final String EXPIRED_JWT_MESSAGE = "인증 정보가 만료되었습니다.";

    @ExceptionHandler(value = ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> expiredJwtExceptionHandling() {
        return getExceptionResponse(EXPIRED_JWT_MESSAGE, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ExceptionResponse> applicationExceptionHandling(ApplicationException exception) {
        var errorMessage = exception.getMessage();
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
        return ResponseEntity.status(status).body(response);
    }
}

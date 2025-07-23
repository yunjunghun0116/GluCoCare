package com.glucocare.server.exception;

public record ExceptionResponse(
        Integer status,
        String message
) {
}

package com.glucocare.server.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    public ErrorMessage message;

    public ApplicationException(ErrorMessage message) {
        this.message = message;
    }
}

package com.glucocare.server.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    public ErrorMessage errorMessage;

    public ApplicationException(ErrorMessage message) {
        this.errorMessage = message;
    }
}

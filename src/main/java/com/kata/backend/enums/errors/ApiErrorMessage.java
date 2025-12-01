package com.kata.backend.enums.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiErrorMessage {

    UNAUTHORIZED("Full authentication is required to access this resource."),
    TOKEN_EXPIRED("The token has expired."),
    FORBIDDEN("You don't have permission to access this resource.")

    ;

    private final String message;

}

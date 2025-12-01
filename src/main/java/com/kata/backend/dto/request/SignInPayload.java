package com.kata.backend.dto.request;

public record SignInPayload(
        String email,
        String password
) {
}

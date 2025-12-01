package com.kata.backend.client.oauth2;

public record TokenResponse(
        String access_token,
        String refresh_token
) {
}

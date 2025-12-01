package com.kata.backend.client.oauth2;
import lombok.Builder;

@Builder
public record TokenParam(

        String client_id,
        String grant_type,
        String username,
        String password
) {
}

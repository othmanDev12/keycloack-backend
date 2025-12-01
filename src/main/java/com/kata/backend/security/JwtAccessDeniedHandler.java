package com.kata.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.backend.enums.errors.ApiErrorMessage;
import com.kata.backend.exceptions.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
@Lazy
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull AccessDeniedException e) throws IOException {
        ApiError error = ApiError.builder()
                .status(HttpStatus.FORBIDDEN)
                .message(ApiErrorMessage.FORBIDDEN.getMessage())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        OutputStream outs = response.getOutputStream();
        objectMapper.writeValue(outs, error);
        outs.flush();
    }
}

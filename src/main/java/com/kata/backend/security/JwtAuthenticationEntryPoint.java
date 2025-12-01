package com.kata.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.backend.enums.errors.ApiErrorMessage;
import com.kata.backend.exceptions.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;


@Component
@Slf4j
@Lazy
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;


    @Override
    public void commence(@NotNull HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        log.error("Responding with unauthorized error. Message - {}", e.getMessage());

        boolean expiredOrNotValid = detectTokenExpired(e);
        ApiError error = ApiError.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message(expiredOrNotValid
                        ? ApiErrorMessage.TOKEN_EXPIRED.getMessage()
                        : ApiErrorMessage.UNAUTHORIZED.getMessage())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        OutputStream outs = response.getOutputStream();
        objectMapper.writeValue(outs, error);
        outs.flush();
    }

    private boolean detectTokenExpired(Throwable t) {
        String description = extractDescription(t);
        return isExpiredToken(description);
    }

    private String extractDescription(Throwable t) {
        OAuth2AuthenticationException oauth2 = findCause(t, OAuth2AuthenticationException.class);
        if (oauth2 != null && oauth2.getError() != null) {
            return oauth2.getError().getDescription() != null
                    ? oauth2.getError().getDescription().toLowerCase()
                    : "";
        }

        JwtException jwtEx = findCause(t, JwtException.class);
        if (jwtEx != null) {
            return jwtEx.getMessage() != null
                    ? jwtEx.getMessage().toLowerCase()
                    : "";
        }

        return "";
    }

    private boolean isExpiredToken(String description) {
        return description.contains("expired") || description.contains("exp");
    }


    @SuppressWarnings("unchecked")
    private <T extends Throwable> T findCause(Throwable t, Class<T> clazz) {
        while (t != null) {
            if (clazz.isInstance(t)) {
                return (T) t;
            }
            t = t.getCause();
        }
        return null;
    }
}

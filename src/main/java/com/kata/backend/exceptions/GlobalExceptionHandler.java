package com.kata.backend.exceptions;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_INTERNAL_SERVER_ERROR_DESCRIPTION = "Error occurred while handling requests. Please try again later.";

    @Override
    protected ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.valueOf(422))
                .message("Validation error. Check 'errors' field for details.")
                .build();
        apiError.setErrors(apiError.generateValidationError(ex.getBindingResult().getFieldErrors()));
        return ResponseEntity.status(422).body(apiError);
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<@NonNull Object> handleExceptionInternal(Exception ex) {
        log.error("An InternalServerException occurred: {} [Class: {}]", ex.getMessage(), ex.getClass().getName(), ex);
        ApiError error = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : DEFAULT_INTERNAL_SERVER_ERROR_DESCRIPTION)
                .build();
        return ResponseEntity.internalServerError()
                .body(error);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<@NonNull Object> handleHttpMessageNotReadable(RuntimeException ex) {
        log.warn("A RuntimeException occurred: {} [Class: {}]", ex.getMessage(), ex.getClass().getName());
        final var error = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(StringUtils.hasText(ex.getMessage()) ? ex.getMessage() : "An unexpected runtime error occurred.")
                .build();
        return ResponseEntity.badRequest()
                .body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<@NonNull Object> handleAuthenticationFailedException(AuthenticationException ex) {
        return buildErrorResponse(ex, HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<@NonNull Object> handleAccessDeniedException(AccessDeniedException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }


    private ResponseEntity<@NonNull Object> buildErrorResponse(Exception exception, @NonNull HttpStatus status) {
        return buildErrorResponse(exception.getMessage(), status);
    }

    private ResponseEntity<@NonNull Object> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(
                ApiError.builder()
                        .status(status)
                        .message(message)
                        .build()
        );
    }
}

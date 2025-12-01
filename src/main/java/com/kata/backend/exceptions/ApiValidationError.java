package com.kata.backend.exceptions;

import lombok.Builder;

@Builder
public record ApiValidationError(String field, String object, String message, Object rejectedValue) {}


package com.kata.backend.model;

import lombok.Builder;

@Builder
public record User(String firstName, String lastName, String email, String password) {
}

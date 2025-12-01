package com.kata.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    ADMIN("ADMIN"),
    GUEST("GUEST"),
    USER("USER");

    private final String roleName;
}

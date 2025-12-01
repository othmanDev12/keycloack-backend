package com.kata.backend.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeycloakAttributes {

    RESOURCE_ACCESS("resource_access"),
    REALM_ACCESS("realm_access"),
    ACCOUNT("account"),
    ROLES("roles"),
    ROLE_PREFIX("ROLE_"),
    CLIENT_ID("client_id"),
    CLIENT_SECRET("client_secret"),
    GRANT_TYPE("grant_type"),
    USERNAME("username"),
    PASSWORD("password"),
    EMAIL("email"),
    GIVEN_NAME("given_name"),
    FAMILY_NAME("family_name"),
    NAME("name")

    ;

    private final String attribute;
}

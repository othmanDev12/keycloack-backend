package com.kata.backend.dto.response;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public record RolesAndAuthorities(Set<GrantedAuthority> authorities, Set<String> roles) {

}

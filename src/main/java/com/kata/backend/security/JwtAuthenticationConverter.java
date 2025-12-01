package com.kata.backend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kata.backend.utils.JwtTokenUtil.extractResourceRoles;
import static com.kata.backend.utils.JwtTokenUtil.getByClaimName;

@Component
@RequiredArgsConstructor
@Lazy
@Slf4j
public class JwtAuthenticationConverter implements Converter<@NonNull Jwt, @NonNull AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    @Override
    public @NonNull AbstractAuthenticationToken convert(final Jwt source) {
        Set<GrantedAuthority> authorities = Stream.concat(
                Optional.of(jwtGrantedAuthoritiesConverter.convert(source))
                        .stream()
                        .flatMap(Collection::stream),
                extractResourceRoles(source).authorities().stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities, getByClaimName(source, JwtClaimNames.SUB));
    }

}

package com.kata.backend.utils;

import com.kata.backend.dto.response.RolesAndAuthorities;
import com.kata.backend.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kata.backend.utils.enums.KeycloakAttributes.*;
import static com.kata.backend.utils.enums.KeycloakAttributes.ROLES;
import static com.kata.backend.utils.enums.KeycloakAttributes.ROLE_PREFIX;

public class JwtTokenUtil {

    private JwtTokenUtil() {

    }

    public static Optional<Jwt> getJwtAuthenticationToken() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(authentication -> ((JwtAuthenticationToken) authentication).getToken());
    }

    public static RolesAndAuthorities extractResourceRoles(Jwt jwt) {
        Set<String> roles = Stream.of(RESOURCE_ACCESS.getAttribute(), REALM_ACCESS.getAttribute())
                .flatMap(claimName -> extractRolesFromClaim(jwt, claimName,
                        RESOURCE_ACCESS.getAttribute().equals(claimName) ? ACCOUNT.getAttribute() : null).stream())
                .collect(Collectors.toSet());

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX.getAttribute() + role))
                .collect(Collectors.toSet());

        return new RolesAndAuthorities(authorities, roles);
    }

    public static Collection<String> extractRolesFromClaim(Jwt jwt, String claimName, String nestedKey) {
        return Optional.ofNullable(jwt.<Map<String, Object>>getClaim(claimName))
                .map(claim -> nestedKey != null ? claim.get(nestedKey) : claim)
                .filter(Map.class::isInstance)
                .map(m -> (Map<?, ?>) m)
                .flatMap(map -> Optional.ofNullable(map.get(ROLES.getAttribute())))
                .filter(Collection.class::isInstance)
                .map(c -> (Collection<?>) c)
                .map(coll -> coll.stream()
                        .map(Objects::toString)
                        .filter(Objects::nonNull)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    public static Set<Role> mapToRoles(Jwt jwt) {
        Set<String> roleNames = extractResourceRoles(jwt).roles();
        Map<String, Role> nameToRole = Arrays.stream(Role.values())
                .collect(Collectors.toMap(Enum::name, r -> r));
        return roleNames.stream()
                .map(nameToRole::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static String getByClaimName(Jwt jwt, String claimName) {
        return jwt.getClaimAsString(claimName);
    }

    public static boolean isExpired(Jwt jwt) {
        Instant exp = jwt.getExpiresAt();
        return exp != null && exp.isBefore(Instant.now());
    }

    public static Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.replaceFirst("(?i)^Bearer\\s+", ""));
    }





}

package com.kata.backend.service.auth;

import com.kata.backend.client.oauth2.Oauth2TokenClient;
import com.kata.backend.client.oauth2.TokenResponse;
import com.kata.backend.dto.request.SignInPayload;
import com.kata.backend.dto.response.UserInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import static com.kata.backend.utils.JwtTokenUtil.*;
import static org.keycloak.OAuth2Constants.*;
import static org.keycloak.representations.IDToken.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Oauth2TokenClient oauth2TokenClient;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;
    @Value("${app.keycloak.admin.clientId}")
    private String clientId;


    @Override
    public TokenResponse authenticate(SignInPayload signInPayload) {
        MultiValueMap<@NonNull String, String> form = new LinkedMultiValueMap<>();
        form.add(CLIENT_ID, clientId);
        form.add(CLIENT_SECRET, clientSecret);
        form.add(GRANT_TYPE, PASSWORD);
        form.add(USERNAME, signInPayload.email());
        form.add(PASSWORD, signInPayload.password());
        return oauth2TokenClient.authenticate(form)
                .orElseThrow(() -> new RuntimeException("Authentication failed"));
    }

    @Override
    public UserInfo getUserInfo() {
        return getJwtAuthenticationToken()
                .map(jwt -> new UserInfo(
                        getByClaimName(jwt, JwtClaimNames.SUB),
                        getByClaimName(jwt, SCOPE_EMAIL),
                        getByClaimName(jwt, GIVEN_NAME),
                        getByClaimName(jwt, FAMILY_NAME),
                        getByClaimName(jwt, NAME),
                        mapToRoles(jwt)
                ))
                .orElse(null);
    }
}

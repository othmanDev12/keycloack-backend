package com.kata.backend.service.auth;

import com.kata.backend.client.oauth2.TokenResponse;
import com.kata.backend.dto.request.SignInPayload;
import com.kata.backend.dto.response.UserInfo;

public interface AuthService {

    TokenResponse authenticate(SignInPayload signInPayload);

    UserInfo getUserInfo();

}

package com.kata.backend.controller;

import com.kata.backend.client.oauth2.TokenResponse;
import com.kata.backend.dto.request.SignInPayload;
import com.kata.backend.dto.response.UserInfo;
import com.kata.backend.service.auth.AuthService;
import com.kata.backend.service.user.UserManagementService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    private final UserManagementService userManagementService;

    @PostMapping("/login")
    public ResponseEntity<@NonNull TokenResponse> authenticate(final @Valid @RequestBody SignInPayload payload) {
        return ResponseEntity.ok(authService.authenticate(payload));
    }


    @GetMapping("/userinfo")
    public ResponseEntity<@NonNull UserInfo> getUserInfo() {
        return ResponseEntity.ok(authService.getUserInfo());
    }


}

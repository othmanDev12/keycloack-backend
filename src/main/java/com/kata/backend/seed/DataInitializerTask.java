package com.kata.backend.seed;

import com.kata.backend.enums.Role;
import com.kata.backend.service.user.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerTask {

    private final UserManagementService userService;
    private final DataProperties properties;

    @Async
    public void init() {
        initRoles();
        initUserData();
    }


    private void initRoles() {
        properties.getRoles().forEach(role -> userService.getRoleByName(role.name())
                .ifPresentOrElse(
                        r -> log.info("Role {} has been already created", r.getName()),
                        () -> {
                            userService.createRole(role);
                            log.info("Role {} created successfully", role.name());
                        }
                ));
    }

    private void initUserData() {
        userService.getUserResourceByEmail(properties.getUser().email())
                .ifPresentOrElse(
                        user -> log.info("User {} has been already created", user.getId()),
                        () -> {
                            Map<String, String> result = userService.createUser(properties.getUser());
                            userService.assignRoleToUser(result.get("data"), Role.ADMIN.name());
                        }
                );
    }
}

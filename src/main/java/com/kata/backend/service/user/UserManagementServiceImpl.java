package com.kata.backend.service.user;

import com.kata.backend.dto.response.UserInfo;
import com.kata.backend.enums.Role;
import com.kata.backend.model.User;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static com.kata.backend.service.user.builder.UserManagementBuilder.buildRoleRepresentation;
import static com.kata.backend.service.user.builder.UserManagementBuilder.buildUserRepresentation;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementServiceImpl implements UserManagementService {

    @Value("${app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;
    @Qualifier("authenticatedUser")
    private final UserInfo authenticatedUser;

    @Override
    public Map<String, String> createUser(User user) {
        UserRepresentation userRep = buildUserRepresentation(user);
        UsersResource userResource = getUsersResource();
        userResource.create(userRep);
        return Map.of("message", "User created successfully", "data",
                getUserResourceByEmail(user.email())
                .map(UserRepresentation::getId)
                .orElse(""));
    }

    @Override
    public void createRole(Role role) {
        RoleRepresentation roleRep = buildRoleRepresentation(role);
        RolesResource rolesResource = getRolesResource();
        rolesResource.create(roleRep);
    }

    @Override
    public Optional<UserRepresentation> getUserResourceByEmail(String email) {
        try {
            UsersResource usersResource = getUsersResource();
            return usersResource.searchByEmail(email, true)
                    .stream()
                    .findFirst();
        } catch (ProcessingException e) {
            return Optional.empty();
        }
    }


    @Override
    public Optional<RoleRepresentation> getRoleByName(String role) {
        try {
            RolesResource rolesResource = getRolesResource();
            return Optional.ofNullable(rolesResource.get(role).toRepresentation());
        } catch (NotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> assignRoleToUser(String userId, String role) {
        return getRoleByName(role)
                .map(roleRep -> assignRoleToUserResource(userId, roleRep))
                .orElseThrow(() -> new NotFoundException("Role not found"));
    }

    @Override
    public UserResource getUserResourceById(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    @Override
    public String getAuthenticatedUserId() {
        return authenticatedUser.getId();
    }

    private Map<String, String> assignRoleToUserResource(String userId, RoleRepresentation roleRep) {
        UserResource userResource = getUserResourceById(userId);
        userResource.roles().realmLevel().add(
                Collections.singletonList(roleRep)
        );
        return Map.of("message", "Role assigned successfully");
    }


    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }

}

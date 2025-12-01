package com.kata.backend.service.user;

import com.kata.backend.enums.Role;
import com.kata.backend.model.User;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Map;
import java.util.Optional;

public interface UserManagementService {

    Map<String, String> createUser(User user);

    void createRole(Role role);

    Optional<UserRepresentation> getUserResourceByEmail(String email);

    Optional<RoleRepresentation> getRoleByName(String role);

    Map<String, String> assignRoleToUser(String userId, String role);

    UserResource getUserResourceById(String id);

    String getAuthenticatedUserId();


}

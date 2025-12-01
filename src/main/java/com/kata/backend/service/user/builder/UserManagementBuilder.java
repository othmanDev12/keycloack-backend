package com.kata.backend.service.user.builder;

import com.kata.backend.enums.Role;
import com.kata.backend.model.User;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public class UserManagementBuilder {

    private UserManagementBuilder() {

    }


    public static UserRepresentation buildUserRepresentation(User user) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setFirstName(user.firstName());
        userRep.setUsername(user.email().toLowerCase());
        userRep.setLastName(user.lastName());
        userRep.setEmail(user.email().toLowerCase());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        userRep.setCredentials(List.of(buildPasswordCredential(user.password())));
        return userRep;
    }

    public static RoleRepresentation buildRoleRepresentation(Role role) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(role.name());
        return roleRepresentation;
    }

    private static CredentialRepresentation buildPasswordCredential(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        return credential;
    }


}

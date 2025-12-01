package com.kata.backend.dto.response;

import com.kata.backend.enums.Role;
import lombok.*;

import java.util.Set;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Set<Role> roles;
}
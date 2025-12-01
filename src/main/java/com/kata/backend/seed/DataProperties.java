package com.kata.backend.seed;

import com.kata.backend.enums.Role;
import com.kata.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Configuration
@ConfigurationProperties("data.init")
public class DataProperties {
    private User user;
    private List<Role> roles;
}

package com.kata.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kata.backend.dto.response.UserInfo;
import com.kata.backend.service.auth.AuthService;
import org.springframework.context.annotation.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Lazy
public class AppBeansConfig {

    @Bean
    public JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JsonMapper()
                .registerModule(new JavaTimeModule());
    }

    @Bean("authenticatedUser")
    @Description("Load Authenticated User")
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserInfo authenticatedUser(AuthService service) {
        return service.getUserInfo();
    }
}

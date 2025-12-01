package com.kata.backend.client.oauth2;

import com.kata.backend.utils.FeignUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(
        name = "token-client",
        url = "${app.keycloak.serverUrl}",
        fallbackFactory = Oauth2TokenClientFallbackFactory.class
)
public interface Oauth2TokenClient {

    @PostMapping(value = "/realms/${app.keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    Optional<TokenResponse> authenticate(@RequestBody MultiValueMap<@NonNull String, String> formData);

}


@Lazy
@Component
class Oauth2TokenClientFallbackFactory implements FallbackFactory<Oauth2TokenClient> {
    @Override
    public Oauth2TokenClient create(Throwable cause) {
        return new OauthTokenClientServiceFallback(cause);
    }
}

@Slf4j
record OauthTokenClientServiceFallback(Throwable cause) implements Oauth2TokenClient {
    @Override
    public Optional<TokenResponse> authenticate(@RequestBody MultiValueMap<@NonNull String, String> formData) {
        FeignUtil.handleClientException(cause);
        return Optional.empty();
    }
}

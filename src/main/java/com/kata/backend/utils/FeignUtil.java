package com.kata.backend.utils;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
public final class FeignUtil {

    private FeignUtil() {

    }

    public static HttpHeaders createHttpHeadersFrom(Map<String, Collection<String>> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        for (Map.Entry<String, Collection<String>> entry : headers.entrySet()) {
            httpHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return httpHeaders;
    }


    public static Collection<String> addTemplateParameter(Collection<String> possiblyNull, String paramName) {
        Collection<String> params = ofNullable(possiblyNull).map(ArrayList::new).orElse(new ArrayList<>());
        params.add(String.format("{%s}", paramName));
        return params;
    }


    public static void handleClientException(Throwable cause) {
        if (cause instanceof FeignException ex) {
            if (ex.status() == 404) {
                log.warn("404 error took place when we called {}", ex.getLocalizedMessage());
            } else if (ex.status() == 400) {
                log.warn("400 error took place when we called {}", ex.getLocalizedMessage());
            } else {
                log.warn("{} error took place when we called: {}\n Request: {}\n Response: {}\n",
                        ex.status(), ex.request().url(), new String(ex.request().body()),
                        ex.responseBody().map(byteBuffer -> StandardCharsets.UTF_8.decode(byteBuffer).toString())
                                .orElse(null)
                );
            }
        } else {
            log.warn("Can't access address client message{}", cause.getLocalizedMessage());
        }
    }
}

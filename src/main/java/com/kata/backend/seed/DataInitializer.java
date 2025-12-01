package com.kata.backend.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class DataInitializer {


    private final DataInitializerTask initializerTask;
    private final ExecutorService executorService;

    @Bean
    public ApplicationRunner runner() {
        return args -> executorService.execute(initializerTask::init);
    }
}

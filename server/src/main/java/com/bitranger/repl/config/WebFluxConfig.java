package com.bitranger.repl.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Hooks;

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * @author bin.zhang
 */
@EnableConfigurationProperties
@RestControllerAdvice
@Configuration
@EnableWebFlux
@Slf4j
public class WebFluxConfig implements WebFluxConfigurer, InitializingBean {

    @Resource
    private ReplProperties sandboxProperties;


    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins(sandboxProperties.getCors().getAllowedOrigins().toArray(new String[0]))
                .maxAge(3600);
    }

    @Bean
    WebSessionIdResolver cookieWebSessionIdResolver() {
        CookieWebSessionIdResolver cookieWebSessionIdResolver = new CookieWebSessionIdResolver();
        cookieWebSessionIdResolver.setCookieName("ADAPAY_SANDBOX_SESSION_ID");
        return cookieWebSessionIdResolver;
    }

    @Bean(destroyMethod = "shutdown")
    ThreadPoolExecutor replThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(sandboxProperties.getRunner().getNumberMax(), new CustomizableThreadFactory("repl-pool-"));
    }

    @Override
    public void afterPropertiesSet() {
        Consumer<? super Throwable> hook = e -> {
            if (!(e.getCause() != null && e.getCause() instanceof InterruptedException)) {
                log.error("Operator called default onErrorDropped", e);
            }
        };
        Hooks.onErrorDropped(hook);
    }
}

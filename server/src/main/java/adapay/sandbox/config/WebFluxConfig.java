package adapay.sandbox.config;

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

import javax.annotation.Resource;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author bin.zhang
 */
@EnableConfigurationProperties
@RestControllerAdvice
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Resource
    private SandboxProperties sandboxProperties;


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
        return new ScheduledThreadPoolExecutor(32, new CustomizableThreadFactory("repl-pool-"));
    }
}

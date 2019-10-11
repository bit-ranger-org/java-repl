package com.bitranger.repl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bin.zhang
 */
@ConfigurationProperties(prefix = "sandbox")
@Component
@Data
public class ReplProperties {

    private String workDir;

    private Server server;

    private Runner runner;

    private Cors cors;

    @Data
    public static class Server {
        private int timeoutSeconds;
    }

    @Data
    public static class Runner {

        private int numberMax;

        private String jarPath;

        private int timeoutSeconds;

        private List<String> jvmOptions;
    }

    @Data
    public static class Cors {

        private List<String> allowedOrigins;
    }
}

package adapay.sandbox.config;

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
public class SandboxProperties {

    private String workDir;

    private String runnerJarPath;

    private int runnerTimeoutSeconds;

    private List<String> runnerJvmOptions;

    private Cors cors;

    @Data
    public static class Cors {

        private List<String> allowedOrigins;
    }
}

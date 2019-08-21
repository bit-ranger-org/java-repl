package adapay.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author bin.zhang
 */
@SpringBootApplication
public class SandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SandboxApplication.class, args);
    }


    @Bean(destroyMethod = "shutdown")
    ThreadPoolExecutor replThreadPoolExecutor() {
        return new ScheduledThreadPoolExecutor(8, new CustomizableThreadFactory("repl-pool-"));
    }

}

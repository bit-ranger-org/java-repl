package adapay.sandbox;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author bin.zhang
 */
@SpringBootApplication
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        log.info(String.format("java.home=%s", System.getProperty("java.home")));
        log.info("adapay sandbox server started");
    }


}

package com.bitranger.repl;

import com.bitranger.repl.java.JavaClassRunner;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * @author bin.zhang
 */
public class Main {

    public static void main(String[] args) {
        String classDir = args[0];
        String className = args[1];
        long timeoutSeconds = Long.parseLong(args[2]);

        Mono.just(className)
                .publishOn(Schedulers.immediate())
                .doOnNext(new JavaClassRunner(classDir)::run)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .doOnError(
                        e -> e instanceof TimeoutException,
                        e -> {
                            System.err.println(String.format("ERROR over time limit %s seconds", timeoutSeconds));
                            System.exit(2);
                        })
                .doOnError(e -> !(e instanceof TimeoutException),
                        e -> {
                            e.getCause().printStackTrace();
                            System.exit(1);
                        })
                .subscribe();

        System.exit(0);
    }
}

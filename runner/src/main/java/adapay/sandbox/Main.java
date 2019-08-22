package adapay.sandbox;

import adapay.sandbox.java.JavaClassRunner;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * @author bin.zhang
 */
public class Main {

    public static void main(String[] args) {
        Mono.just(args[2])
                .publishOn(Schedulers.immediate())
                .doOnNext(new JavaClassRunner(args[1])::run)
                .timeout(Duration.ofSeconds(Long.valueOf(args[0])))
                .doOnError(
                        e -> e instanceof TimeoutException,
                        e -> {
                            System.err.println(String.format("ERROR over time limit %s seconds", args[0]));
                            System.exit(2);
                        })
                .subscribe();
    }
}

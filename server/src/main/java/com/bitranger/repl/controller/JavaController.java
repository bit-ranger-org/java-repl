package com.bitranger.repl.controller;


import com.bitranger.repl.config.ReplProperties;
import com.bitranger.repl.java.JavaSnippetRepl;
import com.bitranger.repl.model.Output;
import com.bitranger.repl.model.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "v1/repl/java", produces = {MediaType.APPLICATION_JSON_VALUE})
@Slf4j
public class JavaController implements InitializingBean {

    @Resource
    private JavaSnippetRepl javaSnippetReplService;

    @Resource
    private ThreadPoolExecutor replThreadPoolExecutor;

    @Resource
    private ReplProperties sandboxProperties;

    private Scheduler scheduler;

    @PostMapping(value = "snippet", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<Output> post(@RequestBody Mono<Snippet> snippet) {
        return snippet.publishOn(scheduler)
                .map(s -> javaSnippetReplService.repl(s))
                .timeout(Duration.ofSeconds(sandboxProperties.getServer().getTimeoutSeconds()))
                .doOnError(e -> !(e instanceof TimeoutException), e -> log.error("repl error", e))
                .onErrorMap(e -> e instanceof TimeoutException || e.getCause() instanceof InterruptedException,
                        e -> new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, HttpStatus.REQUEST_TIMEOUT.getReasonPhrase()));
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler = Schedulers.fromExecutor(replThreadPoolExecutor);
    }
}

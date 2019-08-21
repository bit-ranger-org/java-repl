package adapay.sandbox.java;


import adapay.sandbox.model.Output;
import adapay.sandbox.model.Snippet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping(value = "v1/repl/java", produces = {MediaType.APPLICATION_JSON_VALUE})
public class JavaController implements InitializingBean {

    @Resource
    private JavaSnippetRepl javaSnippetReplService;

    @Resource
    private ThreadPoolExecutor replThreadPoolExecutor;

    private Scheduler scheduler;

    @PostMapping(value = "snippet", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<Output> post(@RequestBody Mono<Snippet> snippet) {
        return snippet.publishOn(scheduler)
                .map(s -> javaSnippetReplService.repl(s));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scheduler = Schedulers.fromExecutor(replThreadPoolExecutor);
    }
}

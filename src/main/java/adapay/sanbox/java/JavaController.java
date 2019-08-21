package adapay.sanbox.java;


import adapay.sanbox.model.Output;
import adapay.sanbox.model.Snippet;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "v1/repl/java", produces = {MediaType.APPLICATION_JSON_VALUE})
public class JavaController {

    @PostMapping(value = "snippet", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Mono<Output> post(@RequestBody Snippet snippet) {
        return Mono.just(null);
    }
}

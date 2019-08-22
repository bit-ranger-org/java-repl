package adapay.sandbox.sanbox.java;

import adapay.sandbox.java.JavaSnippetRepl;
import adapay.sandbox.model.Output;
import adapay.sandbox.model.Snippet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SandboxJavaApplicationTests {

    @Resource
    private JavaSnippetRepl javaSnippetRepl;

    @Test
    public void repl() {

        Output output = javaSnippetRepl.repl(new Snippet(Snippet.Language.JAVA, "System.out.println(\"hello world\");\ntry{Thread.sleep(6000);}catch(Exception e){}"));
        System.out.println(output);
    }

}

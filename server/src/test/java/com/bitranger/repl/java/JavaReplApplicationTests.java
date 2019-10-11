package com.bitranger.repl.java;

import com.bitranger.repl.model.Output;
import com.bitranger.repl.model.Snippet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.annotation.Resource;

@EnableConfigurationProperties
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class JavaReplApplicationTests {

    @Resource
    private JavaSnippetRepl javaSnippetRepl;

    //    @Test
    public void repl() {

        Output output = javaSnippetRepl.repl(new Snippet(Snippet.Language.JAVA, "System.out.println(\"hello world\");\ntry{Thread.sleep(6000);}catch(Exception e){}"));
        System.out.println(output);
    }

}

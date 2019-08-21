package adapay.sanbox.java;

import adapay.sanbox.model.Output;
import adapay.sanbox.model.Snippet;
import reactor.core.publisher.Mono;

import javax.tools.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * @author bin.zhang
 */
public class JavaSnippetReplService {

    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private String workDir = "/Doc/MyRepo/learning-java/compiler/target/classes";

    public Mono<Output> compile(Mono<Snippet> snippet) throws Exception {

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.CHINESE, StandardCharsets.UTF_8)) {
            File file = new File("/Download/javac/Hello.java");
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(file));

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    Arrays.asList("-d", workDir),
                    null,
                    compilationUnits);

            task.call();

            StringWriter writer = new StringWriter();
            PrintWriter out = new PrintWriter(writer);
            Output.Status status = diagnostics.getDiagnostics().size() == 0 ? Output.Status.DONE : Output.Status.COMPILE_FAILURE;
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                out.format("Error on line %d in %s\n%s\n",
                        diagnostic.getLineNumber(), diagnostic.getSource().toUri(), diagnostic.getMessage(null));
            }

            Output output = new Output(status, writer.toString());
            return Mono.just(output);
        }


//        Class clz = Class.forName("Hello");
//        Method main = clz.getMethod("main", String[].class);
//        args = new String[]{"aaa", "bbb"};
//        main.invoke(clz, (Object) args);
    }
}
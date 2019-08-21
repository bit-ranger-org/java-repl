package adapay.sandbox.java;

import adapay.sandbox.model.Output;
import adapay.sandbox.model.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

/**
 * @author bin.zhang
 */
@Service
@Slf4j
public class JavaSnippetRepl {

    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private String workDir = "D:\\Download\\sandbox-java";

    private String codeStructure;

    private int lineNumBefore;

    private ThreadLocalPrintStream threadLocalPrintStream = new ThreadLocalPrintStream();

    private PrintStream originSystemOut = System.out;

    private URLClassLoader urlClassLoader;

    {
        System.setOut(threadLocalPrintStream);

        File srcFile = new File(workDir + File.separator + "structure.tpl");
        try {
            codeStructure = FileUtils.readFileToString(srcFile, StandardCharsets.UTF_8);
            String[] lines = codeStructure.split("\r\n|\r|\n");
            lineNumBefore = lines.length - 3;
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + workDir + File.separator + "classes" + File.separator)});
        } catch (IOException e) {
            throw new RuntimeException("failed to read tpl, cause: " + e.getMessage());
        }
    }

    public Output repl(Snippet snippet) {

        String className = "SANDBOX_SRC_" + UUID.randomUUID().toString().replace("-", "_");
        String completeCode = String.format(codeStructure, className, snippet.getContent());

        File srcFile = new File(workDir + File.separator + "src" + File.separator + className + ".java");
        try {
            try {
                FileUtils.write(srcFile, completeCode, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Output output = compile(srcFile);

            if (output.getStatus() != Output.Status.DONE) {
                return output;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream p = new PrintStream(baos);
            Output.Status status = run(className, p);
            String content = null;
            try {
                content = baos.toString(StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            return new Output(status, content);
        } finally {
            FileUtils.deleteQuietly(srcFile);
            File classFile = new File(workDir + File.separator + "classes" + File.separator + className + ".class");
            FileUtils.deleteQuietly(classFile);
        }
    }


    private Output compile(File srcFile) {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.CHINESE, StandardCharsets.UTF_8)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(srcFile));

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    Arrays.asList("-d", workDir + File.separator + "classes"),
                    null,
                    compilationUnits);
            task.setLocale(Locale.ENGLISH);
            task.call();

            if (diagnostics.getDiagnostics().size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    builder.append(String.format("%s %d,%d %s\n",
                            diagnostic.getKind(),
                            diagnostic.getLineNumber() - lineNumBefore,
                            diagnostic.getColumnNumber(),
                            diagnostic.getMessage(Locale.ENGLISH)));
                }

                return new Output(Output.Status.COMPILE_FAILURE, builder.toString());
            } else {
                return new Output(Output.Status.DONE, "");
            }
        } catch (IOException e) {
            return new Output(Output.Status.COMPILE_FAILURE, e.getMessage());
        }
    }

    private Output.Status run(String className, PrintStream p) {
        Runnable runnable = null;
        try {
            Class clz = Class.forName(className, true, urlClassLoader);
            runnable = (Runnable) clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            threadLocalPrintStream.setPrintStream(p);
            runnable.run();
            return Output.Status.DONE;
        } catch (Exception e) {
            e.printStackTrace(p);
            return Output.Status.EXCEPTION;
        } finally {
            threadLocalPrintStream.setPrintStream(originSystemOut);
        }
    }
}
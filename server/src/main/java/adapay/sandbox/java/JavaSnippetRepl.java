package adapay.sandbox.java;

import adapay.sandbox.model.Output;
import adapay.sandbox.model.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    {
        File srcFile = new File(workDir + File.separator + "structure.tpl");
        try {
            codeStructure = FileUtils.readFileToString(srcFile, StandardCharsets.UTF_8);
            String[] lines = codeStructure.split("\r\n|\r|\n");
            lineNumBefore = lines.length - 3;
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

            Output outputCompile = compile(srcFile);

            if (outputCompile.getStatus() != Output.Status.DONE) {
                return outputCompile;
            }

            return run(className);
        } finally {
            FileUtils.deleteQuietly(srcFile);
            File classFile = new File(workDir + File.separator + "classes" + File.separator + className + ".class");
//            FileUtils.deleteQuietly(classFile);
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

    private Output run(String className) {
        ProcessBuilder builder = new ProcessBuilder(
                "java",
                "-jar",
                "D:\\Doc\\GitRepo\\sandbox-java\\runner\\target\\runner-0.0.1-SNAPSHOT.jar",
                className,
                "1");
        builder.redirectErrorStream(false);
        builder.directory(new File(workDir));
        Process process = null;
        InputStream pi = null;
        Output.Status status = null;
        try {
            process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                pi = process.getInputStream();
                status = Output.Status.DONE;
            } else {
                pi = process.getErrorStream();
                status = Output.Status.EXCEPTION;
            }
            List<String> lines = IOUtils.readLines(pi, StandardCharsets.UTF_8);
            log.debug(lines.toString());
            return new Output(status, lines.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(pi);
            if (process != null) {
                process.destroy();
            }
        }


    }
}
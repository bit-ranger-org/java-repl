package adapay.sandbox.java;

import adapay.sandbox.config.SandboxProperties;
import adapay.sandbox.model.Output;
import adapay.sandbox.model.Snippet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class JavaSnippetRepl implements InitializingBean {

    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    @Resource
    private SandboxProperties sandboxProperties;

    private String codeStructure;

    private int lineNumBefore;

    private String classpath;


    public Output repl(Snippet snippet) {

        String className = "SANDBOX_SRC_" + UUID.randomUUID().toString().replace("-", "_");
        String completeCode = String.format(codeStructure, className, snippet.getContent());

        File srcFile = new File(sandboxProperties.getWorkDir() + File.separator + "src" + File.separator + className + ".java");
        try {
            try {
                FileUtils.write(srcFile, completeCode, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Output outputCompile = compile(srcFile);

            if (outputCompile.getStatus() != Output.Status.SUCCESS) {
                return outputCompile;
            }
            return run(className);
        } finally {
            FileUtils.deleteQuietly(srcFile);
            File classFile = new File(sandboxProperties.getWorkDir() + File.separator + "classes" + File.separator + className + ".class");
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
                    Arrays.asList(
                            "-d",
                            sandboxProperties.getWorkDir() + File.separator + "target",
                            "-classpath",
                            classpath),
                    null,
                    compilationUnits);
            task.setLocale(Locale.ENGLISH);
            task.call();

            if (diagnostics.getDiagnostics().size() > 0) {
                List<String> el = new ArrayList<>();
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    el.add(String.format("%s %d,%d %s\n",
                            diagnostic.getKind(),
                            diagnostic.getLineNumber() - lineNumBefore,
                            diagnostic.getColumnNumber(),
                            diagnostic.getMessage(Locale.ENGLISH)));
                }

                return new Output(Output.Status.COMPILE_FAILURE, Collections.emptyList(), el);
            } else {
                return new Output(Output.Status.SUCCESS, Collections.emptyList(), Collections.emptyList());
            }
        } catch (Throwable e) {
            log.error("compile failure", e);
            throw new RuntimeException("compile failure", e);
        }
    }

    private Output run(String className) {
        List<String> commands = new ArrayList<>();
        commands.add("java");
        commands.addAll(sandboxProperties.getRunnerJvmOptions());
        commands.add("-jar");
        commands.add(sandboxProperties.getRunnerJarPath());
        commands.add(sandboxProperties.getWorkDir() + File.separator + "target");
        commands.add(className);
        commands.add(String.valueOf(sandboxProperties.getRunnerTimeoutSeconds()));
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(false);
        builder.directory(new File(sandboxProperties.getWorkDir()));
        Process process = null;
        InputStream pio = null;
        InputStream pie = null;
        Output.Status status = null;
        try {
            process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                status = Output.Status.SUCCESS;
            } else {
                status = Output.Status.EXCEPTION;
            }
            pio = process.getInputStream();
            pie = process.getErrorStream();
            List<String> ol = IOUtils.readLines(pio, StandardCharsets.UTF_8);
            List<String> oe = IOUtils.readLines(pie, StandardCharsets.UTF_8);

            String stackTraceDisplayRoot = String.format("at %s.run(%s.java:", className, className);
            List<String> olDisplay = new ArrayList<>();
            List<String> oeDisplay = new ArrayList<>();
            for (String l : ol) {
                if (StringUtils.startsWith(StringUtils.strip(l), stackTraceDisplayRoot)) {
                    break;
                }
                olDisplay.add(l);
            }
            for (String l : oe) {
                if (StringUtils.startsWith(StringUtils.strip(l), stackTraceDisplayRoot)) {
                    break;
                }
                oeDisplay.add(l);
            }
            return new Output(status, olDisplay, oeDisplay);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(pio, pie);
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File srcFile = new File(sandboxProperties.getWorkDir() + File.separator + "structure.java");
        try {
            codeStructure = FileUtils.readFileToString(srcFile, StandardCharsets.UTF_8);
            String[] lines = codeStructure.split("\r\n|\r|\n");
            lineNumBefore = lines.length - 3;
        } catch (IOException e) {
            throw new RuntimeException("failed to read tpl, cause: " + e.getMessage());
        }
        List<String> classpathList = new ArrayList<>();
        String classesDir = sandboxProperties.getWorkDir() + File.separator + "classes";
        classpathList.add(classesDir);
        String libDir = sandboxProperties.getWorkDir() + File.separator + "lib";
        Collection<File> jars = FileUtils.listFiles(new File(libDir), new String[]{"jar"}, false);
        jars.forEach(f -> classpathList.add(f.getPath()));

        classpath = StringUtils.join(classpathList, ";");

    }
}
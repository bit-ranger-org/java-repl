package adapay.sandbox.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author bin.zhang
 */
public class JavaClassRunner {

    private URLClassLoader urlClassLoader;

    public JavaClassRunner(String classDir) {
        try {
            urlClassLoader = new URLClassLoader(new URL[]{new URL("file:" + classDir + File.separator)});
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void run(String className) {
        Runnable runnable = null;
        try {
            Class clz = Class.forName(className, true, urlClassLoader);
            runnable = (Runnable) clz.newInstance();
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
package adapay.sandbox.java;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class ThreadLocalPrintStream extends PrintStream {

    private ThreadLocal<PrintStream> printStreamThreadLocal = new ThreadLocal<>();

    public ThreadLocalPrintStream() {
        super(new DiscardOutputStream());
    }

    public void setPrintStream(PrintStream ps) {
        printStreamThreadLocal.set(ps);
    }

    @Override
    public void flush() {
        printStreamThreadLocal.get().flush();
    }

    @Override
    public void close() {
        printStreamThreadLocal.get().close();
    }

    @Override
    public boolean checkError() {
        return printStreamThreadLocal.get().checkError();
    }

    @Override
    public void write(int b) {
        printStreamThreadLocal.get().write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        printStreamThreadLocal.get().write(buf, off, len);
    }

    @Override
    public void print(boolean b) {
        printStreamThreadLocal.get().print(b);
    }

    @Override
    public void print(char c) {
        printStreamThreadLocal.get().print(c);
    }

    @Override
    public void print(int i) {
        printStreamThreadLocal.get().print(i);
    }

    @Override
    public void print(long l) {
        printStreamThreadLocal.get().print(l);
    }

    @Override
    public void print(float f) {
        printStreamThreadLocal.get().print(f);
    }

    @Override
    public void print(double d) {
        printStreamThreadLocal.get().print(d);
    }

    @Override
    public void print(char[] s) {
        printStreamThreadLocal.get().print(s);
    }

    @Override
    public void print(String s) {
        printStreamThreadLocal.get().print(s);
    }

    @Override
    public void print(Object obj) {
        printStreamThreadLocal.get().print(obj);
    }

    @Override
    public void println() {
        printStreamThreadLocal.get().println();
    }

    @Override
    public void println(boolean x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(char x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(int x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(long x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(float x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(double x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(char[] x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(String x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public void println(Object x) {
        printStreamThreadLocal.get().println(x);
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        return printStreamThreadLocal.get().printf(format, args);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        return printStreamThreadLocal.get().printf(l, format, args);
    }

    @Override
    public PrintStream format(String format, Object... args) {
        return printStreamThreadLocal.get().format(format, args);
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        return printStreamThreadLocal.get().format(l, format, args);
    }

    @Override
    public PrintStream append(CharSequence csq) {
        return printStreamThreadLocal.get().append(csq);
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        return printStreamThreadLocal.get().append(csq, start, end);
    }

    @Override
    public PrintStream append(char c) {
        return printStreamThreadLocal.get().append(c);
    }

    private static class DiscardOutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {

        }
    }
}

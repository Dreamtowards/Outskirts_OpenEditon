package outskirts.util.logging;

import outskirts.util.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private PrintStream filestream;

    int stackDeep = 2;

    public Logger(File logfile) {
        if (logfile == null) {
            filestream = null;
            return;
        }

        this.filestream = prepareLoggingPrintStream(logfile);
    }

    static PrintStream prepareLoggingPrintStream(File logfile) {
        try {
            File outputDir = logfile.getParentFile();
            FileUtils.mkdirs(outputDir);

            if (logfile.exists()) {
                String prefix = new SimpleDateFormat("yyyy-MM-dd").format(new Date(logfile.lastModified()));
                int suffix = FileUtils.listFiles(outputDir, file -> file.getName().startsWith(prefix), false).size();
                FileUtils.move(logfile, new File(outputDir, prefix + "-" + suffix + ".log"));
            }

            return new PrintStream(new FileOutputStream(logfile), true, "UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create logging PrintStream.", ex);
        }
    }

    public void info(Object msg, Object... args) {
        println(String.format("[%s][%s][%s/INFO]: %s", time(), stack(), thread(), message(msg, args)),
                System.out);
    }

    public void warn(Object msg, Object... args) {
        println(String.format("[%s][%s][%s/WARN]: %s", time(), stack(), thread(), message(msg, args)),
                System.err);
    }

    private void println(String message, PrintStream consoleStream) {
        consoleStream.println(message);
        if (filestream != null) {
            filestream.println(message);
        }
    }

    private String message(Object msg, Object... args) {
        if (msg == null)
            return  "null";

        try {
            return String.format(msg.toString(), args);
        } catch (Throwable t) {
            return msg.toString();
        }
    }

    private String thread() {
        return Thread.currentThread().getName();
    }

    private String time() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String stack() {
        StackTraceElement stackTrace = new Throwable().getStackTrace()[stackDeep];
        return stackTrace.getFileName() + ":" + stackTrace.getLineNumber();
    }
}

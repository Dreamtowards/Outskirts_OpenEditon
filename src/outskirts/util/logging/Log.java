package outskirts.util.logging;

import outskirts.util.CombinerOutputStream;

import java.io.File;
import java.io.PrintStream;

public final class Log {

    private static Logger LOGGER = new Logger(null);

    static {
        LOGGER.stackDeep = 3;

        PrintStream stream = Logger.prepareLoggingPrintStream(new File("logs/latest.log"));

        System.setOut(new PrintStream(new CombinerOutputStream(System.out, stream)));
        System.setErr(new PrintStream(new CombinerOutputStream(System.err, stream)));
    }

    public static void info(Object msg, Object... args) {
        LOGGER.info(msg, args);
    }

    public static void warn(Object msg, Object... args) {
        LOGGER.warn(msg, args);
    }

}

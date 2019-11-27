package outskirts.util;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import outskirts.util.logging.Log;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class SystemUtils {

    private static String _GET_OS_NAME() {
        String osname = System.getProperty("os.name").toLowerCase();

        if (osname.contains("win")) {
            return "Windows";
        } else if (osname.contains("mac")) {
            return "Mac";
        } else if (osname.contains("linux") || osname.contains("unix")) {
            return "Linux";
        } else {
            return "Unknown";
        }
    }

    public static final String OS_NAME = _GET_OS_NAME();

    public static final boolean IS_OS_MAC = OS_NAME.equals("Mac");
    public static final boolean IS_OS_WINDOWS = OS_NAME.equals("Windows");
    public static final boolean IS_OS_LINUX = OS_NAME.equals("Linux");

    public static void alert(String title, String message) {
        Sys.alert(title, message);
    }

    public static boolean openURL(String url) {
        return Sys.openURL(url);
    }

    public static boolean isCtrlKeyDown() {
        if (IS_OS_MAC) {
            return Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
        } else {
            return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        }
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    public static String getClipboard() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static void setClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
    }


    /**
     * Ignore if the classpath is already loaded.
     */
    public static synchronized void addClasspath(File classpath) {
        try {
            URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL pathURL = classpath.toURI().toURL();

            //if the classpath already been loaded
            if (CollectionUtils.contains(loader.getURLs(), pathURL)) {
                Log.warn("Already added classpath \"%s\". Ignore", pathURL);
                return;
            }

            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, pathURL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getProgramLocation(Class clazz) {
        try {
            return URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    /* Spin-yield loop based alternative to Thread.sleep
     * Based on the code of Andy Malakov
     * http://andy-malakov.blogspot.fr/2010/06/alternative-to-threadsleep.html
     */
    private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
    private static final long SPIN_YIELD_PRECISION = TimeUnit.MILLISECONDS.toNanos(2);
    public static void nanoSleep(long nanoseconds) throws InterruptedException {
        final long end = System.nanoTime() + nanoseconds;
        long remained = nanoseconds;
        do {
            if (remained > SLEEP_PRECISION) {
                Thread.sleep(1);
            } else {
                if (remained > SPIN_YIELD_PRECISION) {
                    Thread.yield();
                }
            }
            remained = end - System.nanoTime();

            if (Thread.interrupted())
                throw new InterruptedException();
        } while (remained > 0);
    }
}

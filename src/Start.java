import outskirts.client.main.Main;
import outskirts.util.CollectionUtils;
import outskirts.util.FileUtils;
import outskirts.util.SystemUtils;
import outskirts.util.logging.Log;

import java.io.File;
import java.util.Arrays;

/**
 * tmp launcher class. simple setup and launch main program
 */
public class Start {

    public static void main(String[] args) {

        if (CollectionUtils.contains(args, "--loadlibs")) { //tmp arg
            for (File file : FileUtils.listFiles(new File("libraries"))) {
                SystemUtils.addClasspath(file);
            }
        }

        //-Dfile.encoding=UTF-8

        //-DsocksProxyHost=localhost
        //-DsocksProxyPort=1080

        System.setProperty("org.lwjgl.librarypath", new File("libraries/lwjgl-2.9.3/windows").getAbsolutePath());

        Main.main(args);

    }

}

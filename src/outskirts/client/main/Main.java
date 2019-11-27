package outskirts.client.main;

import outskirts.client.GameSettings;
import outskirts.client.Outskirts;
import outskirts.util.logging.Log;

import java.io.File;

/**
 * A indie class to bootstrap client Outskirts
 * That will more clear.
 */
public class Main {

    public static void main(String[] args) {

        GameSettings.ProgramArguments.readArguments(args);

        new Outskirts().run();
    }

}

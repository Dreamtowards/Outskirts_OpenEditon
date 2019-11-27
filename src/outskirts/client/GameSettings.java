package outskirts.client;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScreen;
import outskirts.client.gui.screen.GuiScreenDebug;
import outskirts.client.gui.screen.GuiScreenOSX;
import outskirts.event.Events;
import outskirts.event.world.block.BlockChangedEvent;
import outskirts.init.Blocks;
import outskirts.util.IOUtils;
import outskirts.util.KeyBinding;
import outskirts.util.Maths;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector4f;
import outskirts.world.World;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GameSettings {

    // CommandLineArguments or ProgramArguments ..?
    public static class ProgramArguments {

        public static int WIDTH = 700;

        public static int HEIGHT = 500;

        public static void readArguments(String[] args)
        {
            parse(args).forEach((key, value) -> {
                switch (key) {
                    case "width":
                        WIDTH = Integer.parseInt(value);
                        break;
                    case "height":
                        HEIGHT = Integer.parseInt(value);
                        break;
                    default:
                        Log.warn("Unknown program argument: %s", key);
                        break;
                }
            });
        }

        /**
         * the arguments like --width="730" --height="500" --fullscreen
         */
        private static Map<String, String> parse(String[] args) {
            Map<String, String> map = new HashMap<>();
            for (String item : args) {
                if (!item.startsWith("--")) {
                    Log.warn("Unsupported program argument format: \"%s\", skipped.", item);
                    continue;
                }
                if (item.contains("=")) {
                    String key = item.substring("--".length(), item.indexOf("="));
                    String value = item.substring(item.indexOf("=") + 1);
                    map.put(key, value);
                } else {
                    String key = item.substring("--".length());
                    map.put(key, null);
                }
            }
            return map;
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    private @interface Save
    {
        String value();
    }



    @Save("fps_cap")
    public static int FPS_CAPACITY = 60; // cap <= 0 means not lim

    public static boolean ENABLE_VSYNC = false;

    public static float FOV = 75;

    public static float NEAR_PLANE = 0.1f;

    public static float FAR_PLANE = 1000f;



    public static float GUI_SCALE = 1;




    public static int VIEW_DISTANCE_CHUNKS = 2;



    public static int PICKER_DEPTH = 4;



    public static final KeyBinding KEY_ESC = new KeyBinding("key.esc", Keyboard.KEY_ESCAPE, KeyBinding.TYPE_KEYBOARD, "categories.misc").setOnInputListener(keyState -> {
        if (keyState) {
            Outskirts.displayScreen(Outskirts.currentScreen() == null ? new GuiScreenOSX() : null);
        }
    });

    public static final KeyBinding KEY_GUI_DEBUG = new KeyBinding("key.debug", Keyboard.KEY_F3, KeyBinding.TYPE_KEYBOARD, "categories.misc").setOnInputListener(keyState -> {
        if (keyState) {
            Gui.toggleVisible(Outskirts.getIngameGUI().getGuiScreenDebug());
        }
    });


    public static final KeyBinding KEY_PLACING = new KeyBinding("key.placing", KeyBinding.MOUSE_RIGHT, KeyBinding.TYPE_MOUSE, "categories.gameplay").setOnInputListener(keyState -> {
        if (keyState && Outskirts.getWorld() != null && Outskirts.currentScreen() == null) {
            World world = Outskirts.getWorld();

            Vector4f blockPos = Outskirts.getRayPicker().getCurrentBlockPos();

            if (blockPos != null) {
                world.setBlockState(blockPos, Blocks.BRICK.getDefaultState());
            }
        }
    });


    public static final KeyBinding KEY_WALK_FORWARD = new KeyBinding("key.forward", Keyboard.KEY_W, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_BACKWARD = new KeyBinding("key.backward", Keyboard.KEY_S, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_LEFT = new KeyBinding("key.left", Keyboard.KEY_A, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");
    public static final KeyBinding KEY_WALK_RIGHT = new KeyBinding("key.right", Keyboard.KEY_D, KeyBinding.TYPE_KEYBOARD, "categories.gameplay");



    private static final File OPTION_FILE = new File("options.dat");

    public static void loadOptions() {
        try {
            JSONObject json = new JSONObject(IOUtils.toString(new FileInputStream(OPTION_FILE)));

            loadJSON(GameSettings.class, json);

            Log.info("Loaded GameSettings options. (%s)", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load GameSettings options.", ex);
        }
    }

    public static void saveOptions() {
        try {
            JSONObject json = new JSONObject();

            saveJSON(GameSettings.class, json);

            IOUtils.write(new ByteArrayInputStream(json.toString(4).getBytes()), new FileOutputStream(OPTION_FILE));

            Log.info("Saved GameSettings options. (%s)", OPTION_FILE);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to save GameSettings options.", ex);
        }
    }


    private static void loadJSON(Class clazz, JSONObject json) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            Save annotation = field.getAnnotation(Save.class);
            if (annotation == null || !json.has(annotation.value()))
                continue;

            Object option = json.get(annotation.value());
            Class<?> fieldtype = field.getType();
            Object value = null;

            if (fieldtype == Integer.TYPE) {
                value = Integer.parseInt(option.toString());
            } else if (fieldtype == Long.TYPE) {
                value = Long.parseLong(option.toString());
            } else if (fieldtype == Float.TYPE) {
                value = Float.parseFloat(option.toString());
            } else if (fieldtype == Boolean.TYPE) {
                value = Boolean.parseBoolean(option.toString());
            } else if (fieldtype == String.class) {
                value = option.toString();
            } else if (fieldtype == JSONObject.class || fieldtype == JSONArray.class) {
                value = option;
            } else {
                throw new UnsupportedOperationException("Unsupported field type (" + fieldtype + ")");
            }

            field.set(clazz, value);
        }
    }

    private static void saveJSON(Class clazz, JSONObject json) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            Save annotation = field.getAnnotation(Save.class);
            if (annotation == null) continue;

            String key = annotation.value();
            Object value = field.get(clazz);
            Object option = null;

            if (value instanceof JSONObject || value instanceof JSONArray) {
                option = value;
            } else {
                option = value.toString();
            }

            json.put(key, option);
        }
    }
}

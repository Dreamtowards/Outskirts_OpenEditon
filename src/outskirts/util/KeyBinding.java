package outskirts.util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import outskirts.util.registry.Registrable;
import outskirts.util.registry.Registry;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Registrable {

    public static final int TYPE_KEYBOARD = 0;
    public static final int TYPE_MOUSE = 1;

    public static final int MOUSE_NONE = -1; //for mouse-movement or mouse-wheeling, but not a really keyCode
    public static final int MOUSE_LEFT = 0;
    public static final int MOUSE_RIGHT = 1;
    public static final int MOUSE_WHEEL = 2;

    public interface OnInputListener {
        /**
         * @param keyState true if keydown, false if keyup
         */
        void onInput(boolean keyState);
    }

    public static final Registry<KeyBinding> REGISTRY = new Registry.RegistrableRegistry<>();

    private String registryID;
    private int keyCode;
    private final int defaultKeyCode;
    private String category;
    private int deviceType;

    private KeyBinding.OnInputListener onInputListener = keyState -> {};

    public KeyBinding(String registryID, int defaultKeyCode, int deviceType, String category) {
        this.registryID = new ResourceLocation(registryID).toString();
        this.keyCode = defaultKeyCode;
        this.defaultKeyCode = defaultKeyCode;
        this.deviceType = deviceType;
        this.category = category;

        //Auto Register
        REGISTRY.register(this);
    }

    @Override
    public String getRegistryID() {
        return registryID;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public int getDefaultKeyCode() {
        return defaultKeyCode;
    }

    public String getCategory() {
        return category;
    }

    public boolean isKeyDown() {
        if (deviceType == TYPE_KEYBOARD) {
            return Keyboard.isKeyDown(keyCode);
        } else if (deviceType == TYPE_MOUSE) {
            return Mouse.isButtonDown(keyCode);
        } else {
            return false;
        }
    }

    public KeyBinding setOnInputListener(OnInputListener listener) {
        this.onInputListener = listener;
        return this;
    }

    public static void postInput(int keyCode, boolean keyState, int deviceType) {
        for (KeyBinding keyBinding : REGISTRY.values()) {
            if (deviceType == keyBinding.deviceType && keyCode == keyBinding.keyCode) {
                keyBinding.onInputListener.onInput(keyState);
            }
        }
    }
}

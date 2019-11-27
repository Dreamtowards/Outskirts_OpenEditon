package outskirts.event.client.input;

import org.lwjgl.input.Keyboard;
import outskirts.event.Event;

public class KeyboardEvent extends Event {

    public int getKey() {
        return Keyboard.getEventKey();
    }

    public boolean getKeyState() {
        return Keyboard.getEventKeyState();
    }
}

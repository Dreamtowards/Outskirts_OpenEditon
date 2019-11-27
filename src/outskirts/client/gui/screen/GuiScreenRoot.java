package outskirts.client.gui.screen;

import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiScreen;
import outskirts.util.Validate;

/**
 * The Singleton Root GUI.
 * Events calls this gui instance, then transfer to children all GUIs
 */
public class GuiScreenRoot extends GuiScreen {

    //the WRAP of ingameGUI, _WRAP.getChildAt(0) is the real ingameGUI
    private final Gui _WRAP_ingameGUI = new Gui();
    private final Gui _WRAP_currentScreen = new Gui();

    public GuiScreenRoot() {
        addGui(_WRAP_ingameGUI);
        addGui(_WRAP_currentScreen);

        setIngameGUI(new GuiScreenInGame());
    }

    public GuiScreen getIngameGUI() {
        return getContentScreen(_WRAP_ingameGUI);
    }

    public GuiScreen getCurrentScreen() {
        return getContentScreen(_WRAP_currentScreen);
    }

    private void setIngameGUI(GuiScreen screen) {
        setContentScreen(_WRAP_ingameGUI, screen);
    }

    public void setCurrentScreen(GuiScreen screen) {
        setContentScreen(_WRAP_currentScreen, screen);
    }




    private static GuiScreen getContentScreen(Gui wrap) {
        return wrap.getChildCount() == 0 ? null : wrap.getChildAt(0);
    }

    private static void setContentScreen(Gui wrap, Gui inner) {
        Validate.isTrue(wrap.getChildCount() <= 1, "_WRAP gui only can have 1 setChild");

        if (wrap.getChildCount() == 1)
            wrap.removeGui(0);

        if (inner != null)
            wrap.addGui(inner);
    }
}

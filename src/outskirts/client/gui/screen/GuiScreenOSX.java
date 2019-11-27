package outskirts.client.gui.screen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import outskirts.client.Outskirts;
import outskirts.client.gui.Gui;
import outskirts.client.gui.GuiMenu;
import outskirts.client.gui.GuiScreen;
import outskirts.client.gui.GuiText;
import outskirts.event.EventHandler;
import outskirts.event.Events;
import outskirts.event.client.input.KeyboardEvent;
import outskirts.util.Colors;
import outskirts.util.KeyBinding;
import outskirts.util.logging.Log;

import javax.swing.*;
import java.lang.annotation.Annotation;

public class GuiScreenOSX extends GuiScreen {

    private GuiText sysText = new GuiText("System") {
        @Override
        public void onDraw() {

            super.onDraw();

            if (isMouseOver()) {
                drawRect(Colors.BLACK40, getX(), getY(), getWidth(), getHeight());
            }
        }
    }.setHeight(20).setWidth(100);

    private GuiMenu sysMenu = new GuiMenu();

    private Gui toolbar = addGui(new Gui() {

        @Override
        public void onDraw() {

            drawRect(Colors.WHITE20, getX(), getY(), getWidth(), getHeight());

            super.onDraw();
        }
    });

    public GuiScreenOSX() {

        toolbar.addGui(sysMenu);
        toolbar.addGui(sysText);

        setWidth(Outskirts.getWidth()).setHeight(Outskirts.getHeight());

        sysMenu.setWidth(100);
        sysText.addOnClickListener(e -> {
            if (e.getButtonState() && e.getMouseButton() == KeyBinding.MOUSE_LEFT) {
                sysMenu.show(sysText.getX(), sysText.getY() + sysText.getHeight());
            }
        });


        GuiMenu.GuiMenuItem itemShutdown = new GuiMenu.GuiMenuItem("Shutdown");
        sysMenu.addGui(itemShutdown);

        GuiMenu.GuiMenuItem itemRestart = new GuiMenu.GuiMenuItem("Restart");
        sysMenu.addGui(itemRestart);

        GuiMenu.GuiMenuItem itemLogout = new GuiMenu.GuiMenuItem("Logout");
        {
            GuiMenu lo = new GuiMenu().setWidth(130);
            lo.addGui(new GuiMenu.GuiMenuItem("FastLogout"));
            lo.addGui(new GuiMenu.GuiMenuItem("NonFastLogout"));
            itemLogout.setSubMenu(lo);
            GuiMenu.GuiMenuItem ano = new GuiMenu.GuiMenuItem("More");
            {
                GuiMenu lo2 = new GuiMenu().setWidth(100);
                lo2.addGui(new GuiMenu.GuiMenuItem("Fast"));
                lo2.addGui(new GuiMenu.GuiMenuItem("NonF"));
                ano.setSubMenu(lo2);
            }
            lo.addGui(ano);
        }
        sysMenu.addGui(itemLogout);

    }

    @Override
    public void onResize() {

        toolbar.setWidth(Outskirts.getWidth()).setHeight(20);

        super.onResize();
    }
}

package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.client.material.Texture;
import outskirts.client.render.renderer.GuiRenderer;
import outskirts.util.Maths;
import outskirts.util.Validate;
import outskirts.util.function.TriConsumer;
import outskirts.util.function.TriFunction;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Gui {

    private int x; //actually is relative-x
    private int y;
    private int width;
    private int height;

    private boolean focused = false;

    /** efforts to onClickEvent... */
    private boolean enable = true;

    /** f not VISIBLE, the gui parent will not call onDraw() automatically */
    private boolean visible = true;

    /** just a attachment */
    private Object tag;

    //they are not tint.(colorMultiply, opacity) cause other renderer would't supports, its high-level stuff

    private Gui parent;
    private List<Gui> children = new ArrayList<>();

    private List<EventListener> listeners = new ArrayList<>();

    public Gui() {
        this(0, 0, 0, 0);
    }

    public Gui(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //////////////////////////// START GLOBAL DRAW ////////////////////////////

//    public static Vector2i drawString(String texts, int x, int y, Vector4f color, int height, boolean centerHorizontal) {
//        if (centerHorizontal) {
//            int textWidth = Outskirts.renderEngine.getFontRenderer().stringWidth(texts, height);
//            x = x - (textWidth / 2);
//        }
//        return Outskirts.renderEngine.getFontRenderer().drawString(texts, x, y, height, color, true);
//    }
//
    public static Vector2i drawString(String text, int x, int y, Vector4f color, int height) {
        Outskirts.renderEngine.getFontRenderer().renderString(text, x, y, height, color);
        return new Vector2i();
    }
    public static Vector2i drawString(String text, int x, int y, Vector4f color) {
        return drawString(text, x, y, color, GuiText.DEFAULT_TEXT_HEIGHT);
    }

    public static void drawRect(Vector4f color, int x, int y, int width, int height) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, GuiRenderer.TEXTURE_WHITE, x, y, width, height, color);
    }

    public static void drawTexture(Texture texture, int x, int y, int width, int height) {
        Outskirts.renderEngine.getGuiRenderer().render(GuiRenderer.MODEL_RECT, texture, x, y, width, height);
    }

    /**
     * A border inner the [x, y, width, height] size
     */
    public static void drawRectBorder(Vector4f color, int x, int y, int width, int height, int thickness) {

        drawRect(color, x, y, width, thickness); //Top
        drawRect(color, x, y + height - thickness, width, thickness); //Bottom

        drawRect(color, x, y + thickness, thickness, height - thickness - thickness); //Left
        drawRect(color, x + width - thickness, y + thickness, thickness, height - thickness - thickness); //Right
    }



    //////////////////////////// END GLOBAL DRAW ////////////////////////////


    //just some tool-type methods, calls some really methods for more-convenient, do not needs override this
    public final <T extends Gui> T addGui(Gui gui) {
        return addGui(gui, getChildCount());
    }

    public <T extends Gui> T addGui(Gui gui, int index) {
        gui.setParent(this);

        children.add(index, gui);

        return (T) gui;
    }

    public <T extends Gui> T getChildAt(int index) {
        return (T) children.get(index);
    }

    public final void setGui(int index, Gui gui) {
        removeGui(index);
        addGui(gui, index);
    }

    public int getChildCount() {
        return children.size();
    }

    public <T extends Gui> T removeGui(int index) {
        children.get(index).setParent(null);
        return (T) children.remove(index);
    }

    public final void removeAllGuis() {
        for (int i = getChildCount() - 1;i >= 0;i--) {
            removeGui(i);
        }
    }

    public Gui getParent() {
        if (parent == null) {
            return EMPTY_PARENT;
        }
        return parent;
    }

    public void setParent(Gui parent) {
        this.parent = parent;
    }

    //TODO: getChildren(): unmodifiableList ..?






    public int getRelativeX() {
        return this.x;
    }

    public <T extends Gui> T setRelativeX(int x) {
        this.x = x;
        return  (T) this;
    }

    /**
     * Get absolute x position.
     */
    public int getX() {
        return getParent().getX() + x;
    }

    public <T extends Gui> T setX(int x) {
        setRelativeX(x - getParent().getX());
        return (T) this;
    }

    public int getRelativeY() {
        return this.y;
    }

    public <T extends Gui> T setRelativeY(int y) {
        this.y = y;
        return  (T) this;
    }

    public int getY() {
        return getParent().getY() + y;
    }

    public <T extends Gui> T setY(int y) {
        setRelativeY(y - getParent().getY());
        return  (T) this;
    }

    public int getWidth() {
        return width;
    }

    public <T extends Gui> T setWidth(int width) {
        this.width = width;
        return (T) this;
    }

    public int getHeight() {
        return height;
    }

    public <T extends Gui> T setHeight(int height) {
        this.height = height;
        return (T) this;
    }





    public boolean isFocused() {
        return focused;
    }

    public <T extends Gui> T setFocused(boolean focused) {
        this.focused = focused;
        return (T) this;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isVisible() {
        return visible;
    }

    public <T extends Gui> T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public Object getTag() {
        return tag;
    }

    public <T extends Gui> T setTag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{x=" + getX() + ", y=" + getY() + ", width=" + getWidth() + ", height=" + getHeight() + "}";
    }






    public final boolean isMouseOver() {
        return isMouseOver(getX(), getY(), getWidth(), getHeight());
    }

    public static boolean isMouseOver(int x, int y, int width, int height) {
        return Outskirts.getMouseX() >= x && Outskirts.getMouseX() < x + width && Outskirts.getMouseY() >= y && Outskirts.getMouseY() < y + height;
    }

    public static boolean isPointOver(int pointX, int pointY, Gui gui) {
        return pointX >= gui.getX() && pointX < gui.getX() + gui.getWidth() && pointY >= gui.getY() && pointY < gui.getY() + gui.getHeight();
    }

    public static Vector2i calculateChildrenBound(Gui parent) {
        Vector2i bound = new Vector2i();
        for (int i = 0;i < parent.getChildCount();i++) {
            Gui child = parent.getChildAt(i);

            bound.x = Math.max(bound.x, child.getRelativeX() + child.getWidth());
            bound.y = Math.max(bound.y, child.getRelativeY() + child.getHeight());
        }
        return bound;
    }

    //this method really necessary?
    public static <T extends Gui> void forChildren(Gui gui, Consumer<T> visitor, boolean includeChildren) {
        for (int i = 0;i < gui.getChildCount();i++) {
            T child = gui.getChildAt(i);

            visitor.accept(child);

            if (includeChildren && child.getChildCount() > 0) {
                forChildren(child, visitor, true);
            }
        }
    }

    //should this..?
    public static void toggleVisible(Gui gui) {
        gui.setVisible(!gui.isVisible());
    }






    private boolean _tmp_hasDraw = false;

    public void onDraw() {
        if (!_tmp_hasDraw) {
            _tmp_hasDraw = true;
            this.onResize(); //they may having some problem: the event is post to children, but children self also having this event to call him and his children, should onAdded event
        }
        performEvent(new BeforeDrawEvent());

        for (Gui child : children) {
            if (child.isVisible()) {
                child.onDraw();
            }
        }
    }

    /**
     * @param mouseButton -1=none, 0=left, 1=right, 2=wheel
     * @param buttonState true=buttonDown, false=buttonUp
     */
    public void onMouse(int mouseButton, boolean buttonState) {
        if (isEnable()) {
            boolean isMouseOver = isMouseOver();
            if (mouseButton != -1) {
                this.setFocused(isMouseOver);
                if (isMouseOver) {
                    performEvent(new MouseClickEvent(mouseButton, buttonState));
                }
            } else {
                //this actually are not fits all cases, like mouse not move, and the gui is moving, the enter event just cant be detected..
                //this move to the on-draw scope likes good, that'll detect each frame
                boolean isPrevMouseOver = isPointOver(
                        Outskirts.getMouseX() - Outskirts.getMouseDX(),
                        Outskirts.getMouseY() - Outskirts.getMouseDY(), this);
                if (isMouseOver && !isPrevMouseOver) {
                    performEvent(new MouseEnteredEvent());
                } else if (!isMouseOver && isPrevMouseOver) {
                    performEvent(new MouseExitedEvent());
                }
            }
        }
        for (Gui gui : children) {
            if (gui.isVisible()) {
                gui.onMouse(mouseButton, buttonState);
            }
        }
    }

    public void onKeyboard(int keyCode, char typedChar, boolean keyState) {
        for (Gui gui : children) {
            gui.onKeyboard(keyCode, typedChar, keyState);
        }
    }

    /**
     * call when DisplayResizedEvent or Gui::onCreate()
     */
    public void onResize() {
        for (Gui gui : children) {
            gui.onResize();
        }
    }










    private static final Gui EMPTY_PARENT = new Gui() {
        @Override public int getX() { return 0; }
        @Override public int getY() { return 0; }
        @Override public int getWidth() { return 0; }
        @Override public int getHeight() { return 0; }
    };





    private void performEvent(Event event) {
        event.ownerGui = this;

        for (int i = 0;i < listeners.size();) { //not use iterator because some eventListener will be add/remove when event execution
            EventListener lsr = listeners.get(i);

            if (lsr.eventClass == event.getClass()) {
                lsr.invoke(event);
            }

            if (event.requiredRemoveCurrLsr()) {
                listeners.remove(i); //not add curr index, cause the item already been removed.
            } else {
                i++;
            }
        }
    }

    private <E extends Event> void attachListener(Class<E> eventClass, Consumer<E> eventListener) {
        listeners.add(new EventListener(eventClass, eventListener));
    }





    public static final BiConsumer<Gui, Float> TRANS_X = (gui, value) -> gui.setX(value.intValue());
    public static final BiConsumer<Gui, Float> TRANS_Y = (gui, value) -> gui.setY(value.intValue());

    //IG means InterpolationGenerator ha
    public static final TriFunction<Float, Float, Float, Float> IG_BACKEASE = (t, a, b) -> Maths.lerp(Maths.backease(t, 1), a, b);
    public static final TriFunction<Float, Float, Float, Float> IG_POWER3 = (t, a, b) -> Maths.lerp(Maths.powerease(t, 3), a, b);
    public static final TriFunction<Float, Float, Float, Float> IG_LINEAR = (t, a, b) -> Maths.lerp(t, a, b);

    /**
     * @param from,to using float num cause sometimes not only considers pixels, and also having some like transparency value, vec4 color transform..
     * @param pass already passed transform time, less than 0 makes delay transform, bigger than 0 makes already transform
     * @param duration transform duration time, seconds
     * @param applicator the transformation interpolation value applicator
     * @param interpolator the interpolation value generator
     */
    public <T extends Gui> T attachTransform(float from, float to, float duration, BiConsumer<Gui, Float> applicator, TriFunction<Float, Float, Float, Float> interpolator, float pass) {
        attachListener(BeforeDrawEvent.class, new Consumer<BeforeDrawEvent>() {

            private float passed = pass;

            @Override
            public void accept(BeforeDrawEvent e) {
                passed += Outskirts.getDelta();
                if (passed < 0f)
                    return;

                float t = passed / duration;

                float v = interpolator.apply(t, from, to);
                applicator.accept(Gui.this, v);

                if (t > 1f) {
                    e.removeCurrentListener();
                }
            }
        });
        return (T) this;
    }


    public <T extends Gui> T addOnClickListener(Consumer<MouseClickEvent> listener) {
        attachListener(MouseClickEvent.class, listener);
        return (T) this;
    }

    public <T extends Gui> T addOnMouseEnteredListener(Consumer<MouseEnteredEvent> listener) {
        attachListener(MouseEnteredEvent.class, listener);
        return (T) this;
    }

    public <T extends Gui> T addOnMouseExitedListener(Consumer<MouseExitedEvent> listener) {
        attachListener(MouseExitedEvent.class, listener);
        return (T) this;
    }




    private final class EventListener {
        private Class eventClass; //the listener event-type
        private Consumer eventHandler;

        private EventListener(Class eventClass, Consumer eventHandler) {
            this.eventClass = eventClass;
            this.eventHandler = eventHandler;
        }

        private void invoke(Event event) {
            eventHandler.accept(event);
        }
    }

    public static abstract class Event {
        private Gui ownerGui;
        private boolean removeCurrentListener = false;
        public Gui getGui() {
            return ownerGui;
        }

        public void removeCurrentListener() {
            this.removeCurrentListener = true;
        }
        private boolean requiredRemoveCurrLsr() {
            boolean r = removeCurrentListener;
            removeCurrentListener = false;
            return r;
        }
    }

    public static class MouseClickEvent extends Event {

        private int mouseButton;
        private boolean buttonState;

        //Mouse.getEventButton();
        public MouseClickEvent(int mouseButton, boolean buttonState) {
            this.mouseButton = mouseButton;
            this.buttonState = buttonState;
        }

        public int getMouseButton() {
            return mouseButton;
        }

        public boolean getButtonState() {
            return buttonState;
        }
    }

    public static class MouseExitedEvent extends Event {

    }

    public static class MouseEnteredEvent extends Event {

    }

    public static class BeforeDrawEvent extends Event {

    }

}

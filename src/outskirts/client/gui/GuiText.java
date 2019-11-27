package outskirts.client.gui;

import outskirts.client.Outskirts;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

public class GuiText extends Gui {

    public static int DEFAULT_TEXT_HEIGHT = 16;

    private String text = "";

    private int textHeight = DEFAULT_TEXT_HEIGHT;

    private Vector4f textColor = new Vector4f(1, 1, 1, 1);

    public GuiText() {}

    public GuiText(String text) {
        setText(text);
    }

    @Override
    public void onDraw() {

        drawString(text, getX(), getY(), textColor, textHeight);

        super.onDraw();
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextColor(Vector4f textColor) {
        this.textColor = textColor;
    }

    public Vector4f getTextColor() {
        return textColor;
    }

    public static void updateTextBound(GuiText guiText) {
        Vector2i bound = Outskirts.renderEngine.getFontRenderer().calculateBound(guiText.getText(), guiText.getTextHeight());

        guiText.setWidth(bound.x).setHeight(bound.y);
    }
}

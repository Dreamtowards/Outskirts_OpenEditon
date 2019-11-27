package outskirts.client.render.renderer;

import outskirts.client.Loader;
import outskirts.client.Outskirts;
import outskirts.client.material.Model;
import outskirts.client.material.Texture;
import outskirts.client.render.shader.ShaderProgram;
import outskirts.util.CollectionUtils;
import outskirts.util.IOUtils;
import outskirts.util.Maths;
import outskirts.util.ResourceLocation;
import outskirts.util.logging.Log;
import outskirts.util.vector.Vector2f;
import outskirts.util.vector.Vector2i;
import outskirts.util.vector.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;

public class FontRenderer extends Renderer {

    private ShaderProgram shader = new ShaderProgram(
            new ResourceLocation("shaders/font.vsh").getInputStream(),
            new ResourceLocation("shaders/font.fsh").getInputStream()
    );

    /**
     * only contact with glyph_widths.bin for calculation
     * char percent width (glyph_widths[unicode]/GLYPH_WIDTH_MAX=[0.0-1.0])
     */
    public static final int GLYPH_WIDTH_MAX = 255;
    private byte[] glyphWidths = new byte[65536];

    private static int GAP_CHAR = 1;
    private static int GAP_LINE = 3;

    private Texture unicodeTextureArray;

    private boolean[] loadedUnicodePages = new boolean[256]; //true means loaded

    public FontRenderer() {

        try {
            new ResourceLocation("font/glyph_widths.bin").getInputStream().read(glyphWidths);
        } catch (Exception ex) {
            throw new RuntimeException("Impossible exception. glyph_widths.bin failed to read");
        }

        loadUnicodePage(255); //alloc TextureArray, 255*16*16 is char max supports
    }

    private void loadUnicodePage(int unicodePage) {
        if (loadedUnicodePages[unicodePage])
            return;
        try {
            BufferedImage bufferedImage = ImageIO.read(new ResourceLocation("textures/fonts/unicode_page_" + unicodePage + ".png").getInputStream());
            unicodeTextureArray = Loader.loadTextureArray(unicodeTextureArray, new BufferedImage[]{bufferedImage}, unicodePage);

            loadedUnicodePages[unicodePage] = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static final int RENDER_INSTANCES_COUNT = 64; // keep sync with shader's array length
    private String[] uniform_color = createUniformNameArray("color", RENDER_INSTANCES_COUNT);
    private String[] uniform_string = createUniformNameArray("string", RENDER_INSTANCES_COUNT);
    private String[] uniform_glyphWidth = createUniformNameArray("glyphWidth", RENDER_INSTANCES_COUNT);
    private String[] uniform_offset = createUniformNameArray("offset", RENDER_INSTANCES_COUNT);

    public void renderString(String text, int x, int y, int textHeight, Vector4f color) {
        Vector2i pointer = new Vector2i(x, y);

        for (int i = 0;i < text.length();i += RENDER_INSTANCES_COUNT) {

            pointer = renderLine(
                    text.substring(i, Math.min(i+RENDER_INSTANCES_COUNT, text.length())),
                    pointer.x, pointer.y, x, textHeight, color);

        }
    }

    private static final Vector2f TMP_NDC_TRANS = new Vector2f();

    private Vector2i renderLine(String line, int x, int y, int lineX, int textHeight, Vector4f color) {

        shader.useProgram();

        shader.setFloat("textHeight", (float)textHeight / Outskirts.getHeight());
        shader.setFloat("textUnitWidth", (float)textHeight / Outskirts.getWidth());

        int startX = x;
        int startY = y;
        for (int i = 0;i < line.length();i++) {
            char ch = line.charAt(i);

            loadUnicodePage(ch / 256);

            float widthRatio = charWidth(ch);

            shader.setVector4f(uniform_color[i], color);
            shader.setInt(uniform_string[i], ch);
            shader.setFloat(uniform_glyphWidth[i], widthRatio);

            shader.setVector2f(uniform_offset[i], Maths.calculateNormalDeviceCoords(startX, startY, Outskirts.getWidth(), Outskirts.getHeight(), TMP_NDC_TRANS));

            startX += (int)(widthRatio * textHeight) + GAP_CHAR;

            if (ch == '\n') {
                startX = lineX;
                startY += textHeight + GAP_LINE;
            }
        }

        Model rect = GuiRenderer.MODEL_RECT;
        glBindVertexArray(rect.vaoID());

        glDrawArraysInstanced(GL_TRIANGLES, 0, rect.vertexCount(), line.length());

        return new Vector2i(startX, startY);
    }

    /**
     * @return 0.0-1.0 percent of max-width
     */
    public float charWidth(char ch) {
        return (glyphWidths[ch] & 0xFF) / (float)GLYPH_WIDTH_MAX;
    }

    public Vector2i calculateBound(String texts, int textHeight) {
        int startX = 0;
        int startY = 0;
        for (int i = 0;i < texts.length();i++) {
            char ch = texts.charAt(i);

            float widthRatio = charWidth(ch);

            startX += (int)(widthRatio * textHeight) + GAP_CHAR;
        }
        startY += textHeight;
        return new Vector2i(startX, startY);
    }


    @Override
    public ShaderProgram getShader() {
        return shader;
    }
}

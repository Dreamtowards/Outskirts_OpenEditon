package outskirts.util;

import outskirts.util.vector.Vector4f;

public class Colors {

    public static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    public static final Vector4f WHITE20 = new Vector4f(1, 1, 1, 0.2f);

    public static final Vector4f BLACK = new Vector4f(0, 0, 0, 1);
    public static final Vector4f BLACK40 = new Vector4f(0, 0, 0, 0.4f);


    public static Vector4f DARK_BLUE = fromRGB(0, 0, 170);    //&1
    public static Vector4f DARK_GREEN = fromRGB(0, 170, 0);   //&2
    public static Vector4f DARK_AQUA = fromRGB(0, 170, 170);  //&3
    public static Vector4f DARK_RED = fromRGB(170, 0, 0);     //&4
    public static Vector4f DARK_PURPLE = fromRGB(170, 0, 170);//&5
    public static Vector4f GOLD = fromRGB(255, 170, 0);       //&6
    public static Vector4f GRAY = fromRGB(170, 170, 170);     //&7
    public static Vector4f DARK_GRAY = fromRGB(85, 85, 85);   //&8
    public static Vector4f BLUE = fromRGB(85, 85, 255);       //&9
    public static Vector4f GREEN = fromRGB(85, 255, 85);      //&a
    public static Vector4f AQUA = fromRGB(85, 255, 255);      //&b
    public static Vector4f RED = fromRGB(255, 85, 85);        //&c
    public static Vector4f PURPLE = fromRGB(288, 85, 255);    //&d
    public static Vector4f YELLOW = fromRGB(255, 255, 85);    //&e

    public static Vector4f UNIT_R = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
    public static Vector4f UNIT_G = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    public static Vector4f UNIT_B = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);

    public static Vector4f of(int rgba) {
        int r = (rgba >> 24) & 0xFF;
        int g = (rgba >> 16) & 0xFF;
        int b = (rgba >> 8) & 0xFF;
        int a = rgba & 0xFF;
        return new Vector4f(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    private static Vector4f fromRGB(int r, int g, int b) {
        return new Vector4f(r / 255f, g / 255f, b / 255f, 1);
    }
}

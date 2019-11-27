package outskirts.util;

import java.util.LinkedList;
import java.util.List;

public class StringUtils {

    public static final String CR = "\r"; //CarriageReturn
    public static final String LF = "\n"; //LineFeed
    public static final String CR_LF = "\r\n";
    public static final String EMPTY = "";
    public static final String SPACE = " ";

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toHexString((b & 0xF0) >> 4));
            sb.append(Integer.toHexString( b & 0x0F));
        }
        return sb.toString();
    }

    /**
     * str:"a = b =c", sep:"="   -> ["a ", " b ", "c"]
     * str:"a = b =c", sep:"b"   -> ["a = ", " =c"]
     * str:"a = b =c", sep:"ABC" -> ["a = b =c"]
     * str:"="       , sep:"="   -> ["", ""]
     */
    public static String[] explode(String str, String separator) {
        List<String> result = new LinkedList<>();
        int pointer = 0;
        int next;
        while ((next = str.indexOf(separator, pointer)) != -1) {
            result.add(str.substring(pointer, next));
            pointer = next + separator.length();
        }
        result.add(str.substring(pointer));
        return result.toArray(new String[0]);
    }

    public static String replaceLast(String str, String target, String replacement) {
        int pos = str.lastIndexOf(target);
        if (pos == -1) return str;

        return str.substring(0, pos) + replacement + str.substring(pos + target.length());
    }

    public static String dup(String c, int amount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < amount;i++)
            sb.append(c);
        return sb.toString();
    }
}

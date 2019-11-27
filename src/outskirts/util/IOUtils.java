package outskirts.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    private static final byte[] DEFAULT_BUFFER = new byte[4096];

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                closeQuietly(closeable);
            }
        }
    }

    public static long write(InputStream inputStream, OutputStream outputStream, byte[] buffer) throws IOException {
        long total = 0;
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
            total += length;
        }
        return total;
    }

    public static long write(InputStream inputStream, OutputStream outputStream) throws IOException {
        return write(inputStream, outputStream, DEFAULT_BUFFER);
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(inputStream, out);
        return out.toByteArray();
    }

    public static FloatBuffer fillBuffer(FloatBuffer buffer, float... data) {
        for (int i = 0;i < data.length;i++) {
            buffer.put(i, data[i]);
        }
        return buffer;
    }

    public static String toString(InputStream inputStream, Charset charset) throws IOException {
        return new String(toByteArray(inputStream), charset);
    }

    public static String toString(InputStream inputStream) throws IOException {
        return toString(inputStream, Charset.defaultCharset());
    }

    public static String toString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }


}

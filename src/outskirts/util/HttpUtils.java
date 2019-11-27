package outskirts.util;

import outskirts.util.logging.Log;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

    private static int DEFAULT_TIMEOUT = 10_000;

    public static String httpGet(String url, @Nullable byte[] body, @Nullable Map<String, String> headers) throws IOException {
        return request(openConnection("GET", url), body == null ? null : new ByteArrayInputStream(body), headers);
    }

    public static String httpGet(String url, byte[] body) throws IOException {
        return httpGet(url, body, null);
    }

    public static String httpGet(String url) throws IOException {
        return httpGet(url, null, null);
    }

    public static String httpPost(String url, byte[] body, Map<String, String> headers) throws IOException {
        return request(openConnection("POST", url), body == null ? null : new ByteArrayInputStream(body), headers);
    }

    public static String httpPost(String url, byte[] body) throws IOException {
        return httpPost(url, body, null);
    }

    public static String request(HttpURLConnection connection, @Nullable InputStream body, @Nullable Map<String, String> headers) throws IOException {

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (body != null) {
            IOUtils.write(body, connection.getOutputStream());
            IOUtils.closeQuietly(connection.getOutputStream());
        }

        connection.connect();

        int code = connection.getResponseCode();
        if (code == 301 || code == 302 || code == 303 || code == 307) {
            String location = connection.getHeaderField("Location");
            Log.warn("[HttpUtils] Redirection %s to URL: %s", code, location);
            return request(openConnection(connection.getRequestMethod(), location), body, headers);
        }

        InputStream is = connection.getInputStream();
        String encoding = connection.getHeaderField("Content-Encoding");
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            is = new GZIPInputStream(is);
        }
        String result = IOUtils.toString(is);
        IOUtils.closeQuietly(is);

        return result;
    }

    public static HttpURLConnection openConnection(String method, String url, int timeout) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.setRequestMethod(method);
        return connection;
    }

    public static HttpURLConnection openConnection(String method, String url) throws IOException {
        return openConnection(method, url, DEFAULT_TIMEOUT);
    }

}

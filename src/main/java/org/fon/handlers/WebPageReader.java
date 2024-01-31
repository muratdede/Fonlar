package org.fon.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class WebPageReader {
    public static String readWebPage(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder returnValue = new StringBuilder();

        int BUFFER_SIZE=1024;
        char[] buffer = new char[BUFFER_SIZE]; // or some other size,
        int charsRead = 0;
        while ( (charsRead  = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
            returnValue.append(buffer, 0, charsRead);
        }

        in.close();
        return returnValue.toString();
    }
}

package org.fon.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebPageReader {
    public static String readWebPage(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder returnValue = new StringBuilder();
        String inputLine = "";
        while ((inputLine = in.readLine()) != null)
            returnValue.append(inputLine);

        in.close();
        return returnValue.toString();
    }
}

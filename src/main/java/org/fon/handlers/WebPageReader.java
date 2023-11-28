package org.fon.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebPageReader {
    public static String readWebPage(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET() // GET is default
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            LogHandler.printStackTrace(e);
            return null;
        } catch (IOException e) {
            LogHandler.println("Connection Failed!");
            return null;
        }

        return response.body();
    }
}

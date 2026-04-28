package org.fon.handlers;

import org.json.JSONObject;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * TEFAS JSON API istemcisi.
 * Session cookie yönetimi ve gerekli HTTP header'ları otomatik olarak ayarlar.
 */
public class TefasApiClient {

    private static final String BASE_URL = "https://www.tefas.gov.tr";
    private static final String COOKIE_INIT_PATH = "/tr/fon-detayli-analiz/";
    private static final String FON_FIYAT_API_PATH = "/api/funds/fonFiyatBilgiGetir";
    private static final String FON_UNVAN_ARA_API_PATH = "/api/funds/fonUnvanAra";

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String REFERER = BASE_URL + "/TarihselVeriler.aspx";

    private static boolean sessionInitialized = false;

    private TefasApiClient() {
        // Utility class
    }

    /**
     * Cookie tabanlı session'ı başlatır. İlk API çağrısından önce bir kez çalıştırılmalıdır.
     */
    private static synchronized void ensureSessionInitialized() throws IOException {
        if (sessionInitialized) {
            return;
        }

        CookieHandler.setDefault(new CookieManager());

        HttpURLConnection connection = createConnection(BASE_URL + COOKIE_INIT_PATH, "GET");
        connection.getResponseCode(); // Cookie'leri al
        connection.disconnect();

        sessionInitialized = true;
    }

    /**
     * Belirli bir fon için fiyat bilgilerini getirir.
     *
     * @param fonKodu fon kodu (örn. "TLY")
     * @param periyod periyod değeri (örn. 12 = son 1 yıl)
     * @return API response JSONObject
     */
    public static JSONObject fetchFonPriceData(String fonKodu, int periyod) throws IOException {
        ensureSessionInitialized();

        JSONObject payload = new JSONObject();
        payload.put("dil", "TR");
        payload.put("fonKodu", fonKodu);
        payload.put("periyod", periyod);

        return postJson(BASE_URL + FON_FIYAT_API_PATH, payload);
    }

    /**
     * Tüm fon listesini unvan araması ile getirir.
     *
     * @return API response JSONObject (resultList içinde fonKodu ve fonUnvan alanları)
     */
    public static JSONObject fetchFonList() throws IOException {
        ensureSessionInitialized();

        JSONObject payload = new JSONObject();
        payload.put("dil", "TR");
        payload.put("aramaMetni", "");
        payload.put("fonTip", JSONObject.NULL);

        return postJson(BASE_URL + FON_UNVAN_ARA_API_PATH, payload);
    }

    private static JSONObject postJson(String urlStr, JSONObject payload) throws IOException {
        HttpURLConnection connection = createConnection(urlStr, "POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("TEFAS API hatası: HTTP " + responseCode + " - " + urlStr);
        }

        String responseBody = readResponse(connection);
        connection.disconnect();

        return new JSONObject(responseBody);
    }

    private static HttpURLConnection createConnection(String urlStr, String method) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Referer", REFERER);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}

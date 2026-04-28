package org.fon.handlers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class TefasParser {

    static final int DEFAULT_PERIYOD = 12;

    public static GraphData parseGraphData(String fonName) {
        try {
            JSONObject response = TefasApiClient.fetchFonPriceData(fonName, DEFAULT_PERIYOD);
            JSONArray resultList = response.getJSONArray("resultList");

            if (resultList.isEmpty()) {
                throw new RuntimeException("API'den veri alınamadı: " + fonName);
            }

            String longFonName = resultList.getJSONObject(0).getString("fonUnvan");
            ObservableList<XYChart.Data<String, Number>> chartData = parseChartData(resultList);

            return new GraphData(new XYChart.Series<>(chartData), fonName, longFonName);

        } catch (IOException e) {
            throw new RuntimeException("TEFAS API bağlantı hatası: " + fonName, e);
        }
    }

    private static ObservableList<XYChart.Data<String, Number>> parseChartData(JSONArray resultList) {
        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();

        for (int i = 0; i < resultList.length(); i++) {
            JSONObject entry = resultList.getJSONObject(i);
            String tarih = entry.getString("tarih");
            double fiyat = entry.getDouble("fiyat");

            chartData.add(new XYChart.Data<>(tarih, fiyat));
        }

        return chartData;
    }

    public static ObservableList<String> getFonList() {
        ObservableList<String> result = FXCollections.observableArrayList();

        try {
            JSONObject response = TefasApiClient.fetchFonList();
            JSONArray resultList = response.getJSONArray("resultList");

            for (int i = 0; i < resultList.length(); i++) {
                result.add(resultList.getJSONObject(i).getString("fonKodu"));
            }
        } catch (IOException e) {
            LogHandler.printStackTrace(e);
        }

        return result;
    }
}

package org.fon.handlers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TefasParser {

    static private final Pattern graphDatePattern = Pattern.compile("xAxis: \\[\\{\"categories\":\\[.*?]");
    static private final Pattern graphPricePattern = Pattern.compile("series: \\[\\{\"name\":\"Fiyat\",\"data\":\\[.*?]");
    static private final Pattern longNamePattern = Pattern.compile("<span id=\"MainContent_FormViewMainIndicators_LabelFund\">.*?</span></h2>");
    static private final Pattern fonListPattern = Pattern.compile("<a href='FonAnaliz\\.aspx\\?FonKod=.*?'>");

    public static GraphData parseGraphData(String fonName) {
        ObservableList<XYChart.Data<String, Number>> results = FXCollections.observableArrayList();
        String longFonName;
        String[] xValues;
        try {
            String content = WebPageReader.readWebPage("https://www.tefas.gov.tr/FonAnaliz.aspx?FonKod=" + fonName);

            Matcher dateMatcher = graphDatePattern.matcher(content);
            if (dateMatcher.find()) {
                String token = dateMatcher.group();
                String valueStr = token.substring(token.lastIndexOf("[") + 1, token.lastIndexOf("]")).replaceAll("\"", "");

                xValues = valueStr.split(",");
            } else {
                throw new RuntimeException("Cannot find dates on the graph!");
            }

            Matcher priceMatcher = graphPricePattern.matcher(content);
            if (priceMatcher.find()) {
                String token = priceMatcher.group();
                String valueStr = token.substring(token.lastIndexOf("[") + 1, token.lastIndexOf("]"));

                String[] values = valueStr.split(",");
                for (int i = 0; i < values.length; i++) {
                    results.add(new XYChart.Data<>(xValues[i], Double.parseDouble(values[i])));
                }
            } else {
                throw new RuntimeException("Cannot find prices on the graph!");
            }

            Matcher longNameMatcher = longNamePattern.matcher(content);
            if (longNameMatcher.find()) {
                String value = longNameMatcher.group();

                longFonName = value.substring(value.lastIndexOf("<span id=\"MainContent_FormViewMainIndicators_LabelFund\">") +
                        "<span id=\"MainContent_FormViewMainIndicators_LabelFund\">".length(), value.lastIndexOf("</span></h2>"));
            } else {
                throw new RuntimeException();
            }

        } catch (IOException e) {
            throw new RuntimeException("Cannot find long name of the fon!");
        }

        return new GraphData(new XYChart.Series<>(results), fonName, longFonName);
    }

    public static ObservableList<String> getFonList() {
        ObservableList<String> result = FXCollections.observableArrayList();

        try {
            String content = WebPageReader.readWebPage("https://www.tefas.gov.tr/FonAnaliz.aspx?");

            Matcher longNameMatcher = fonListPattern.matcher(content);

            while (longNameMatcher.find()) {
                String value = longNameMatcher.group();

                String string = value.replace("<a href='FonAnaliz.aspx?FonKod=", "").replace("'>", "");
                result.add(string);
            }
        } catch (IOException e) {
            LogHandler.printStackTrace(e);
        }

        return result;
    }
}

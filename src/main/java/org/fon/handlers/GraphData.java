package org.fon.handlers;

import javafx.scene.chart.XYChart;

public class GraphData {
    XYChart.Series<String, Number> series;
    String fonName;
    String longFonName;

    GraphData(XYChart.Series<String, Number> series, String fonName, String longFonName) {
        this.series = series;
        this.fonName = fonName;
        this.longFonName = longFonName;
    }

    public XYChart.Series<String, Number> getSeries() {
        return series;
    }

    public String getFonName() {
        return fonName;
    }

    public String getLongFonName() {
        return longFonName;
    }
}

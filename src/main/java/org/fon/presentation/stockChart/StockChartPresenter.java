package org.fon.presentation.stockChart;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;


public class StockChartPresenter implements Initializable {
    @FXML
    public LineChart<String, Number> chart;
    @FXML
    public CategoryAxis xAxis;
    @FXML
    public NumberAxis yAxis;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initializeTable(XYChart.Series<String, Number> series) {
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (int i = 0; i < series.getData().size(); i++) {
            minValue = Math.min((Double) series.getData().get(i).getYValue(), minValue);
            maxValue = Math.max((Double) series.getData().get(i).getYValue(), maxValue);
        };

        double lowerBound = minValue - minValue/10;
        double upperBound = maxValue + maxValue/20;

        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 5);
        yAxis.setTickLabelFont(new Font(13));

        chart.getData().add(series);

        Tooltip tooltip = new Tooltip();
        for (XYChart.Data<String, Number> d : series.getData()) {
            d.getNode().setOnMouseEntered(event -> {
                tooltip.setText(String.format("Fiyat\n%s: %.6f", d.getXValue(), d.getYValue().doubleValue()));
                tooltip.setStyle("-fx-font-size: 13px");

                Bounds nodeBounds = d.getNode().getBoundsInLocal();
                Bounds nodeBoundsInScreen = d.getNode().localToScreen(nodeBounds);
                tooltip.show(d.getNode(),
                        nodeBoundsInScreen.getMaxX()+15,
                        nodeBoundsInScreen.getMaxY()+5);
            });
            d.getNode().setOnMouseExited(event -> tooltip.hide());
        }

    }
}

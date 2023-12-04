package org.fon.models;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyLabel extends Label {

    protected final DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("tr", "TR"));
    protected final DoubleProperty valueProperty = new SimpleDoubleProperty();

    private final Stage popup = new Stage();
    private final Timeline popupTimeline;
    protected final DecimalFormat longFormatter = (DecimalFormat) NumberFormat.getInstance(new Locale("tr", "TR"));

    public MoneyLabel() {
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
        longFormatter.setDecimalFormatSymbols(symbols);
        longFormatter.setMaximumFractionDigits(6);
        longFormatter.setMinimumFractionDigits(6);

        createHoverDisplayBinding();
        createTextBinding();

        //---------
        popup.setResizable(false);
        popup.initStyle(StageStyle.TRANSPARENT);

        Label label = new Label();
        Scene scene = new Scene(label);

        scene.setFill(Color.TRANSPARENT);

        popupTimeline = new Timeline(new KeyFrame(new Duration(1500), event -> {
            Platform.runLater(() -> {
                synchronized (this) {
                    String prefix = "";
                    if (this.getText().contains("-"))
                        prefix = this.getText().substring(0, 2);
                    else
                        prefix = this.getText().substring(0, 1);

                    label.setText(prefix + longFormatter.format(Math.abs(valueProperty.get())));
                    label.setTextFill(this.getTextFill());

                    popup.setScene(scene);

                    popup.show();
                }
            });
        }));
    }

    private void createHoverDisplayBinding() {
        setOnMouseEntered(mouseEvent -> {
            popup.setX(mouseEvent.getScreenX() + 10);
            popup.setY(mouseEvent.getScreenY() + 5);

            popupTimeline.playFromStart();
        });

        setOnMouseExited(mouseEvent -> {
            synchronized (this) {
                popupTimeline.stop();
                popup.close();
            }
        });
    }

    private void createTextBinding() {
        textProperty().bind(Bindings.createStringBinding(() -> {
            double value = valueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
            }
        }, valueProperty));
    }

    public void setValue(double value) {
        valueProperty.set(value);
    }

    public double getValue() {
        return valueProperty.get();
    }

    public DoubleProperty valueProperty() {
        return valueProperty;
    }
}

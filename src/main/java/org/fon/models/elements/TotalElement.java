package org.fon.models.elements;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class TotalElement {
    private ObjectProperty category = new SimpleObjectProperty(this, "category");
    private ObjectProperty totalPrice = new SimpleObjectProperty(this, "totalPrice");
    private ObjectProperty todayTotalPrice = new SimpleObjectProperty(this, "todayTotalPrice");
    private ObjectProperty profit = new SimpleObjectProperty(this, "profit");
    private ObjectProperty profitPercentage = new SimpleObjectProperty(this, "profitPercentage");
    private ObjectProperty changePercentage = new SimpleObjectProperty(this, "changePercentage");
    private ObjectProperty change = new SimpleObjectProperty(this, "change");
    private ObjectProperty demand = new SimpleObjectProperty(this, "demand");

    private Label categoryLabel = null;
    private Label totalPriceLabel = new Label();
    private Label todayTotalPriceLabel = new Label();
    private Label profitLabel = new Label();
    private Label profitPercentageLabel = new Label();
    private Label changePercentageLabel = new Label();
    private Label changeLabel = new Label();
    private Label demandLabel = new Label();

    DoubleProperty totalPriceValueProperty = new SimpleDoubleProperty();
    DoubleProperty totalTodayPriceValueProperty = new SimpleDoubleProperty();
    DoubleProperty profitValueProperty = new SimpleDoubleProperty();
    DoubleProperty profitPercentageValueProperty = new SimpleDoubleProperty();
    DoubleProperty changePercentageValueProperty = new SimpleDoubleProperty();
    DoubleProperty changeValueProperty = new SimpleDoubleProperty();
    DoubleProperty demandValueProperty = new SimpleDoubleProperty();

    private ObservableList<CategoryElement> categoryElementList = null;
    private final InvalidationListener listener = observable -> update();

    public TotalElement(ObservableList<CategoryElement> categoryElementList) {
        this.categoryElementList = categoryElementList;
        setCategory(new Label("Toplam"));
        setTotalPrice(totalPriceLabel);
        setTodayTotalPrice(todayTotalPriceLabel);
        setProfit(profitLabel);
        setProfitPercentage(profitPercentageLabel);
        setChangePercentage(changePercentageLabel);
        setChange(changeLabel);
        setDemand(demandLabel);

        categoryElementList.forEach(fonElement -> {
            fonElement.totalPriceProperty().addListener(listener);
            fonElement.todayTotalPriceProperty().addListener(listener);
            fonElement.changeProperty().addListener(listener);
            fonElement.demandProperty().addListener(listener);
        });
        categoryElementList.addListener((ListChangeListener<? super CategoryElement>) listChange -> {
            listChange.getAddedSubList().forEach((fonElement -> {
                fonElement.totalPriceProperty().addListener(listener);
                fonElement.todayTotalPriceProperty().addListener(listener);
                fonElement.changeProperty().addListener(listener);
                fonElement.demandProperty().addListener(listener);
            }));
            listChange.getRemoved().forEach((fonElement -> {
                fonElement.totalPriceProperty().removeListener(listener);
                fonElement.todayTotalPriceProperty().removeListener(listener);
                fonElement.changeProperty().removeListener(listener);
                fonElement.demandProperty().removeListener(listener);
            }));
        });

        profitValueProperty.bind(Bindings.subtract(totalTodayPriceValueProperty, totalPriceValueProperty));
        profitPercentageValueProperty.bind(Bindings.multiply(Bindings.divide(profitValueProperty, totalPriceValueProperty), 100));
        changePercentageValueProperty.bind(Bindings.multiply(Bindings.divide(changeValueProperty, Bindings.subtract(totalTodayPriceValueProperty, changeValueProperty)), 100));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("tr", "TR"));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        totalPriceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double price = totalPriceValueProperty.get();
            if (price < 0) {
                return "-₺" + formatter.format(-price);
            } else {
                return "₺" + formatter.format(price);
            }
        }, totalPriceValueProperty));

        todayTotalPriceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double price = totalTodayPriceValueProperty.get();
            if (price < 0) {
                return "-₺" + formatter.format(-price);
            } else {
                return "₺" + formatter.format(price);
            }
        }, totalTodayPriceValueProperty));

        profitLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double profit = profitValueProperty.get();
            if (profit < 0) {
                return "-₺" + formatter.format(-profit);
            } else {
                return "₺" + formatter.format(profit);
            }
        }, profitValueProperty));

        profitPercentageLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = profitPercentageValueProperty.get();
            if (Double.isNaN(percentage)) {
                return "%0,00";
            } else if (percentage < 0) {
                return "-%" + formatter.format(-percentage);
            } else {
                return "%" + formatter.format(percentage);
            }
        }, profitPercentageValueProperty));

        changeLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double change = changeValueProperty.get();
            if (Double.isNaN(change)) {
                return "₺0,00";
            } else if (change < 0) {
                return "-₺" + formatter.format(-change);
            } else {
                return "₺" + formatter.format(change);
            }
        }, changeValueProperty));

        changePercentageLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = changePercentageValueProperty.get();
            if (Double.isNaN(percentage)) {
                return "%0,00";
            } else if (percentage < 0) {
                return "-%" + formatter.format(-percentage);
            } else {
                return "%" + formatter.format(percentage);
            }
        }, changePercentageValueProperty));

        demandLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double price = demandValueProperty.get();
            if (price < 0) {
                return "-₺" + formatter.format(-price);
            } else {
                return "₺" + formatter.format(price);
            }
        }, demandValueProperty));

        profitLabel.textFillProperty().bind(Bindings.when(Bindings.lessThan(profitValueProperty, 0))
                .then(Color.RED)
                .otherwise(Color.GREEN));
        profitPercentageLabel.textFillProperty().bind(Bindings.when(Bindings.lessThan(profitPercentageValueProperty, 0))
                .then(Color.RED)
                .otherwise(Color.GREEN));
        changeLabel.textFillProperty().bind(Bindings.when(Bindings.lessThan(changeValueProperty, 0))
                .then(Color.RED)
                .otherwise(Color.GREEN));
        changePercentageLabel.textFillProperty().bind(Bindings.when(Bindings.lessThan(changePercentageValueProperty, 0))
                .then(Color.RED)
                .otherwise(Color.GREEN));
    }

    public void update() {
        Double tmpTotalPrice = 0.0;
        Double tmpTotalTodayPrice = 0.0;
        Double tmpChange = 0.0;
        Double tmpDemand = 0.0;

        for (CategoryElement categoryElementElement : categoryElementList) {
            tmpTotalPrice += categoryElementElement.totalPriceProperty().get().getValue();
            tmpTotalTodayPrice += categoryElementElement.todayTotalPriceProperty().get().getValue();
            tmpChange += categoryElementElement.changeProperty().get().getValue();
            tmpDemand += categoryElementElement.demandProperty().get().getValue();
        }

        totalPriceValueProperty.set(tmpTotalPrice);
        totalTodayPriceValueProperty.set(tmpTotalTodayPrice);
        changeValueProperty.set(tmpChange);
        demandValueProperty.set(tmpDemand);
    }

    public ObjectProperty categoryProperty() {
        return category;
    }

    public void setCategory(Object value) {
        categoryProperty().set(value);
    }

    public Object getCategory() {
        return categoryProperty().get();
    }

    public ObjectProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(Object value) {
        totalPriceProperty().set(value);
    }

    public Object getTotalPrice() {
        return totalPriceProperty().get();
    }

    public ObjectProperty todayTotalPriceProperty() {
        return todayTotalPrice;
    }

    public void setTodayTotalPrice(Object value) {
        todayTotalPriceProperty().set(value);
    }

    public Object getTodayTotalPrice() {
        return todayTotalPriceProperty().get();
    }

    public ObjectProperty profitProperty() {
        return profit;
    }

    public void setProfit(Object value) {
        profitProperty().set(value);
    }

    public Object getProfit() {
        return profitProperty().get();
    }

    public ObjectProperty profitPercentageProperty() {
        return profitPercentage;
    }

    public void setProfitPercentage(Object value) {
        profitPercentageProperty().set(value);
    }

    public Object getProfitPercentage() {
        return profitPercentageProperty().get();
    }

    public ObjectProperty changePercentageProperty() {
        return changePercentage;
    }

    public void setChangePercentage(Object value) {
        changePercentageProperty().set(value);
    }

    public Object getChangePercentage() {
        return changePercentageProperty().get();
    }

    public ObjectProperty changeProperty() {
        return change;
    }

    public void setChange(Object value) {
        changeProperty().set(value);
    }

    public Object getChange() {
        return changeProperty().get();
    }

    public ObjectProperty demandProperty() {
        return demand;
    }

    public void setDemand(Object value) {
        demandProperty().set(value);
    }

    public Object getDemand() {
        return demandProperty().get();
    }
}

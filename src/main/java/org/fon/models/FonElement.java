package org.fon.models;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class FonElement {
    private StringProperty category = new SimpleStringProperty(this, "category");
    private StringProperty name = new SimpleStringProperty(this, "name");
    private IntegerProperty count = new SimpleIntegerProperty(this, "count");
    private ObjectProperty price = new SimpleObjectProperty(this, "price");
    private ObjectProperty totalPrice = new SimpleObjectProperty(this, "totalPrice");
    private ObjectProperty todayPrice = new SimpleObjectProperty(this, "todayPrice");
    private ObjectProperty totalTodayPrice = new SimpleObjectProperty(this, "totalTodayPrice");
    private ObjectProperty profit = new SimpleObjectProperty(this, "profit");
    private ObjectProperty profitPercentage = new SimpleObjectProperty(this, "profitPercentage");
    private ObjectProperty change = new SimpleObjectProperty(this, "change");
    private ObjectProperty changePercentage = new SimpleObjectProperty(this, "changePercentage");
    private ObjectProperty demand = new SimpleObjectProperty(this, "demand");
    private StringProperty lastUpdate = new SimpleStringProperty(this, "lastUpdate");

    private Label priceLabel = new Label();
    private Label totalPriceLabel = new Label();
    private Label todayPriceLabel = new Label();
    private Label totalTodayPriceLabel = new Label();
    private Label profitLabel = new Label();
    private Label profitPercentageLabel = new Label();
    private Label changePercentageLabel = new Label();
    private Label changeLabel = new Label();
    private Label demandLabel = new Label();

    DoubleProperty priceValueProperty = new SimpleDoubleProperty();
    DoubleProperty totalPriceValueProperty = new SimpleDoubleProperty();
    DoubleProperty todayPriceValueProperty = new SimpleDoubleProperty();
    DoubleProperty totalTodayPriceValueProperty = new SimpleDoubleProperty();
    DoubleProperty profitValueProperty = new SimpleDoubleProperty();
    DoubleProperty profitPercentageValueProperty = new SimpleDoubleProperty();
    DoubleProperty changePercentageValueProperty = new SimpleDoubleProperty();
    DoubleProperty changeValueProperty = new SimpleDoubleProperty();
    DoubleProperty demandValueProperty = new SimpleDoubleProperty();

    public FonElement (String category, String name, Integer count, Double price, Double todayPrice, Double changePercentage, Double demand, String lastUpdate) {
        this.price.set(priceLabel);
        this.totalPrice.set(totalPriceLabel);
        this.todayPrice.set(todayPriceLabel);
        this.totalTodayPrice.set(totalTodayPriceLabel);
        this.profit.set(profitLabel);
        this.profitPercentage.set(profitPercentageLabel);
        this.changePercentage.set(changePercentageLabel);
        this.change.set(changeLabel);
        this.demand.set(demandLabel);

        totalPriceValueProperty.bind(Bindings.multiply(this.count, priceValueProperty));
        totalTodayPriceValueProperty.bind(Bindings.multiply(this.count, todayPriceValueProperty));
        profitValueProperty.bind(Bindings.subtract(totalTodayPriceValueProperty, totalPriceValueProperty));
        profitPercentageValueProperty.bind(Bindings.multiply(Bindings.divide(profitValueProperty, totalPriceValueProperty), 100));
        changeValueProperty.bind(Bindings.subtract(totalTodayPriceValueProperty, Bindings.divide(totalTodayPriceValueProperty, Bindings.add(1, Bindings.divide(changePercentageValueProperty, 100)))));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("tr", "TR"));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        priceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double value = priceValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
            }
        }, priceValueProperty));

        totalPriceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double value = totalPriceValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
            }
        }, totalPriceValueProperty));

        todayPriceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double value = todayPriceValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
            }
        }, todayPriceValueProperty));

        totalTodayPriceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double value = totalTodayPriceValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
            }
        }, totalTodayPriceValueProperty));

        profitLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            double value = profitValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
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
            double value = demandValueProperty.get();
            if (value < 0) {
                return "-₺" + formatter.format(-value);
            } else {
                return "₺" + formatter.format(value);
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

        setCategory(category);
        setName(name);
        setCount(count);
        setPrice(price);
        setTodayPrice(todayPrice);
        setChangePercentage(changePercentage);
        setDemand(demand);
        setLastUpdate(lastUpdate);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public void setCategory(String value) {
        categoryProperty().set(value);
    }

    public String getCategory() {
        return categoryProperty().get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String value) {
        nameProperty().set(value);
    }

    public String getName() {
        return nameProperty().get();
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public void setCount(Integer value) {
        countProperty().set(value);
    }

    public Integer getCount() {
        return countProperty().get();
    }

    public ObjectProperty priceProperty() {
        return price;
    }

    public void setPrice(Double value) {
        priceValueProperty.set(value);
    }

    public Double getPrice() {
        return priceValueProperty.get();
    }

    public ObjectProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(Double value) {
        totalPriceValueProperty.set(value);
    }

    public Double getTotalPrice() {
        return totalPriceValueProperty.get();
    }

    public ObjectProperty todayPriceProperty() {
        return todayPrice;
    }

    public void setTodayPrice(Double value) {
        todayPriceValueProperty.set(value);
    }

    public Double getTodayPrice() {
        return todayPriceValueProperty.get();
    }

    public ObjectProperty totalTodayPriceProperty() {
        return totalTodayPrice;
    }

    public void setTotalTodayPrice(Double value) {
        totalTodayPriceValueProperty.set(value);
    }

    public Double getTotalTodayPrice() {
        return totalTodayPriceValueProperty.get();
    }

    public ObjectProperty profitProperty() {
        return profit;
    }

    public void setProfit(Double value) {
        profitValueProperty.set(value);
    }

    public Double getProfit() {
        return profitValueProperty.get();
    }

    public ObjectProperty profitPercentageProperty() {
        return profitPercentage;
    }

    public void setProfitPercentage(Double value) {
        profitPercentageValueProperty.set(value);
    }

    public Double getProfitPercentage() {
        return profitPercentageValueProperty.get();
    }

    public ObjectProperty changePercentageProperty() {
        return changePercentage;
    }

    public void setChangePercentage(Double value) {
        changePercentageValueProperty.set(value);
    }

    public Double getChangePercentage() {
        return changePercentageValueProperty.get();
    }

    public ObjectProperty changeProperty() {
        return change;
    }

    public void setChange(Double value) {
        changeValueProperty.set(value);
    }

    public Double getChange() {
        return changeValueProperty.get();
    }

    public ObjectProperty demandProperty() {
        return demand;
    }

    public void setDemand(Double value) {
        demandValueProperty.set(value);
    }

    public Double getDemand() {
        return demandValueProperty.get();
    }

    public StringProperty lastUpdateProperty() {
        return lastUpdate;
    }

    public void setLastUpdate(String value) {
        lastUpdateProperty().set(value);
    }

    public String getLastUpdate() {
        return lastUpdateProperty().get();
    }

    public String toSqlUpdateString() {
        return "UPDATE \"" + getCategory() + "\" SET" +
                " \"Adet\"=\"" + getCount() + '"' +
                ", \"Birim Maliyet\"=\"" + getPrice() + '"' +
                ", \"Birim Fiyatı\"=\"" + getTodayPrice() + '"' +
                ", \"% Değişim\"=\"" + getChangePercentage() + '"' +
                ", \"Talep Edildi\"=\"" + getDemand() + '"' +
                ", \"Güncellenme Tarihi\"=\"" + getLastUpdate() + '"' +
                " WHERE \"AD\"=\"" + getName() + '"';
    }

    public String toSqlInsertString() {
        return "INSERT INTO \"" + getCategory() + "\" (" +
                " \"AD\"" +
                ", \"Adet\"" +
                ", \"Birim Maliyet\"" +
                ", \"Birim Fiyatı\"" +
                ", \"% Değişim\"" +
                ", \"Talep Edildi\"" +
                ", \"Güncellenme Tarihi\") VALUES (" +
                " \"" + getName() + '"' +
                ", \"" + getCount() + '"' +
                ", \"" + getPrice() + '"' +
                ", \"" + getTodayPrice() + '"' +
                ", \"" + getChangePercentage() + '"' +
                ", \"" + getDemand() + '"' +
                ", \"" + getLastUpdate() + "\")";
    }

    public String toSqlRemoveString() {
        return "DELETE FROM \"" + getCategory() + "\" WHERE \"AD\"=\"" + getName() + "\"";
    }
}

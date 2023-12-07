package org.fon.models.elements;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.fon.models.money.MoneyLabel;
import org.fon.models.money.MoneyPercentageLabel;
import org.fon.models.money.MoneyProfitLabel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;


public class CategoryElement {
    private final ObjectProperty<Label> category = new SimpleObjectProperty<>(this, "category");
    private final ObjectProperty<MoneyLabel> totalPrice = new SimpleObjectProperty<>(this, "totalPrice");
    private final ObjectProperty<MoneyLabel> todayTotalPrice = new SimpleObjectProperty<>(this, "todayTotalPrice");
    private final ObjectProperty<MoneyLabel> profit = new SimpleObjectProperty<>(this, "profit");
    private final ObjectProperty<MoneyLabel> profitPercentage = new SimpleObjectProperty<>(this, "profitPercentage");
    private final ObjectProperty<MoneyLabel> changePercentage = new SimpleObjectProperty<>(this, "changePercentage");
    private final ObjectProperty<MoneyLabel> change = new SimpleObjectProperty<>(this, "change");
    private final ObjectProperty<MoneyLabel> demand = new SimpleObjectProperty<>(this, "demand");

    private ObservableList<FonElement> fonElementList = null;
    private final InvalidationListener listener = observable -> update();

    public CategoryElement(ObservableList<FonElement> fonElementList, String name) {
        this.fonElementList = fonElementList;
        category.set(new Label(name));
        totalPrice.set(new MoneyLabel());
        todayTotalPrice.set(new MoneyLabel());
        profit.set(new MoneyProfitLabel());
        profitPercentage.set(new MoneyPercentageLabel());
        changePercentage.set(new MoneyPercentageLabel());
        change.set(new MoneyProfitLabel());
        demand.set(new MoneyLabel());

        fonElementList.forEach(fonElement -> {
            fonElement.totalPriceProperty().get().valueProperty().addListener(listener);
            fonElement.totalTodayPriceProperty().get().valueProperty().addListener(listener);
            fonElement.changeProperty().get().valueProperty().addListener(listener);
            fonElement.demandProperty().get().valueProperty().addListener(listener);
        });
        fonElementList.addListener((ListChangeListener<? super FonElement>) listChange -> {
            update();
            while (listChange.next()) {
                listChange.getAddedSubList().forEach((fonElement -> {
                    fonElement.totalPriceProperty().get().valueProperty().addListener(listener);
                    fonElement.totalTodayPriceProperty().get().valueProperty().addListener(listener);
                    fonElement.changeProperty().get().valueProperty().addListener(listener);
                    fonElement.demandProperty().get().valueProperty().addListener(listener);
                }));
                listChange.getRemoved().forEach((fonElement -> {
                    fonElement.totalPriceProperty().get().valueProperty().removeListener(listener);
                    fonElement.totalTodayPriceProperty().get().valueProperty().removeListener(listener);
                    fonElement.changeProperty().get().valueProperty().removeListener(listener);
                    fonElement.demandProperty().get().valueProperty().removeListener(listener);
                }));
            }
        });

        profit.get().valueProperty().bind(Bindings.subtract(todayTotalPrice.get().valueProperty(), totalPrice.get().valueProperty()));
        profitPercentage.get().valueProperty().bind(Bindings.multiply(Bindings.divide(profit.get().valueProperty(), totalPrice.get().valueProperty()), 100));
        changePercentage.get().valueProperty().bind(Bindings.multiply(Bindings.divide(change.get().valueProperty(), Bindings.subtract(todayTotalPrice.get().valueProperty(), change.get().valueProperty())), 100));

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(new Locale("tr", "TR"));
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);
    }

    public void update() {
        Double tmpTotalPrice = 0.0;
        Double tmpTotalTodayPrice = 0.0;
        Double tmpChange = 0.0;
        Double tmpDemand = 0.0;

        for (FonElement fonElement : fonElementList) {
            tmpTotalPrice += fonElement.getTotalPrice();
            tmpTotalTodayPrice += fonElement.getTotalTodayPrice();
            tmpChange += fonElement.getChange();
            tmpDemand += fonElement.getDemand();
        }

        totalPrice.get().setValue(tmpTotalPrice);
        todayTotalPrice.get().setValue(tmpTotalTodayPrice);
        change.get().setValue(tmpChange);
        demand.get().setValue(tmpDemand);
    }

    public StringProperty categoryValueProperty() {
        return category.get().textProperty();
    }

    public DoubleProperty totalPriceValueProperty() {
        return totalPrice.get().valueProperty();
    }

    public DoubleProperty todayTotalPriceValueProperty() {
        return todayTotalPrice.get().valueProperty();
    }

    public DoubleProperty profitValueProperty() {
        return profit.get().valueProperty();
    }

    public DoubleProperty profitPercentageValueProperty() {
        return profitPercentage.get().valueProperty();
    }

    public DoubleProperty changePercentageValueProperty() {
        return changePercentage.get().valueProperty();
    }

    public DoubleProperty changeValueProperty() {
        return change.get().valueProperty();
    }

    public DoubleProperty demandValueProperty() {
        return demand.get().valueProperty();
    }


    // ----- FXML reflection functions ----
    public ObjectProperty<Label> categoryProperty() {
        return category;
    }

    public ObjectProperty<MoneyLabel> totalPriceProperty() {
        return totalPrice;
    }

    public ObjectProperty<MoneyLabel> todayTotalPriceProperty() {
        return todayTotalPrice;
    }

    public ObjectProperty<MoneyLabel> profitProperty() {
        return profit;
    }

    public ObjectProperty<MoneyLabel> profitPercentageProperty() {
        return profitPercentage;
    }

    public ObjectProperty<MoneyLabel> changePercentageProperty() {
        return changePercentage;
    }

    public ObjectProperty<MoneyLabel> changeProperty() {
        return change;
    }

    public ObjectProperty<MoneyLabel> demandProperty() {
        return demand;
    }

}

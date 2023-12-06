package org.fon.models.elements;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.fon.models.money.MoneyLabel;
import org.fon.models.money.MoneyPercentageLabel;
import org.fon.models.money.MoneyProfitLabel;

public class TotalElement {
    private final ObjectProperty<Label> category = new SimpleObjectProperty<>(this, "category");
    private final ObjectProperty<MoneyLabel> totalPrice = new SimpleObjectProperty<>(this, "totalPrice");
    private final ObjectProperty<MoneyLabel> todayTotalPrice = new SimpleObjectProperty<>(this, "todayTotalPrice");
    private final ObjectProperty<MoneyLabel> profit = new SimpleObjectProperty<>(this, "profit");
    private final ObjectProperty<MoneyLabel> profitPercentage = new SimpleObjectProperty<>(this, "profitPercentage");
    private final ObjectProperty<MoneyLabel> changePercentage = new SimpleObjectProperty<>(this, "changePercentage");
    private final ObjectProperty<MoneyLabel> change = new SimpleObjectProperty<>(this, "change");
    private final ObjectProperty<MoneyLabel> demand = new SimpleObjectProperty<>(this, "demand");

    private final ObservableList<CategoryElement> categoryElementList;
    private final InvalidationListener listener = observable -> update();

    public TotalElement(ObservableList<CategoryElement> categoryElementList) {
        this.categoryElementList = categoryElementList;

        category.set(new Label("Toplam"));
        totalPrice.set(new MoneyLabel());
        todayTotalPrice.set(new MoneyLabel());
        profit.set(new MoneyProfitLabel());
        profitPercentage.set(new MoneyPercentageLabel());
        changePercentage.set(new MoneyPercentageLabel());
        change.set(new MoneyProfitLabel());
        demand.set(new MoneyLabel());

        this.categoryElementList.forEach(categoryElement -> {
            categoryElement.totalPriceValueProperty().addListener(listener);
            categoryElement.todayTotalPriceValueProperty().addListener(listener);
            categoryElement.changeValueProperty().addListener(listener);
            categoryElement.demandValueProperty().addListener(listener);
        });

        profit.get().valueProperty().bind(Bindings.subtract(todayTotalPrice.get().valueProperty(), totalPrice.get().valueProperty()));
        profitPercentage.get().valueProperty().bind(Bindings.multiply(Bindings.divide(profit.get().valueProperty(), totalPrice.get().valueProperty()), 100));
        changePercentage.get().valueProperty().bind(Bindings.multiply(Bindings.divide(change.get().valueProperty(), Bindings.subtract(todayTotalPrice.get().valueProperty(), change.get().valueProperty())), 100));
    }

    public void update() {
        double tmpTotalPrice = 0.0;
        double tmpTotalTodayPrice = 0.0;
        double tmpChange = 0.0;
        double tmpDemand = 0.0;

        for (CategoryElement categoryElementElement : categoryElementList) {
            tmpTotalPrice += categoryElementElement.totalPriceValueProperty().getValue();
            tmpTotalTodayPrice += categoryElementElement.todayTotalPriceValueProperty().getValue();
            tmpChange += categoryElementElement.changeValueProperty().getValue();
            tmpDemand += categoryElementElement.demandValueProperty().getValue();
        }

        totalPrice.get().setValue(tmpTotalPrice);
        todayTotalPrice.get().setValue(tmpTotalTodayPrice);
        change.get().setValue(tmpChange);
        demand.get().setValue(tmpDemand);
    }

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

package org.fon.models.elements;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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

    private ObservableList<CategoryElement> categoryElementList = null;
    private final InvalidationListener listener = observable -> update();

    public TotalElement(ObservableList<CategoryElement> categoryElementList) {
        this.categoryElementList = categoryElementList;

        setCategory(new Label("Toplam"));
        setTotalPrice(new MoneyLabel());
        setTodayTotalPrice(new MoneyLabel());
        setProfit(new MoneyProfitLabel());
        setProfitPercentage(new MoneyPercentageLabel());
        setChangePercentage(new MoneyPercentageLabel());
        setChange(new MoneyProfitLabel());
        setDemand(new MoneyLabel());

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
            tmpTotalPrice += categoryElementElement.totalPriceProperty().get().getValue();
            tmpTotalTodayPrice += categoryElementElement.todayTotalPriceProperty().get().getValue();
            tmpChange += categoryElementElement.changeProperty().get().getValue();
            tmpDemand += categoryElementElement.demandProperty().get().getValue();
        }

        totalPrice.get().setValue(tmpTotalPrice);
        todayTotalPrice.get().setValue(tmpTotalTodayPrice);
        change.get().setValue(tmpChange);
        demand.get().setValue(tmpDemand);
    }

    public ObjectProperty<Label> categoryProperty() {
        return category;
    }

    public void setCategory(Label value) {
        categoryProperty().set(value);
    }

    public Label getCategory() {
        return categoryProperty().get();
    }

    public ObjectProperty<MoneyLabel> totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(MoneyLabel value) {
        totalPriceProperty().set(value);
    }

    public MoneyLabel getTotalPrice() {
        return totalPriceProperty().get();
    }

    public ObjectProperty<MoneyLabel> todayTotalPriceProperty() {
        return todayTotalPrice;
    }

    public void setTodayTotalPrice(MoneyLabel value) {
        todayTotalPriceProperty().set(value);
    }

    public MoneyLabel getTodayTotalPrice() {
        return todayTotalPriceProperty().get();
    }

    public ObjectProperty<MoneyLabel> profitProperty() {
        return profit;
    }

    public void setProfit(MoneyLabel value) {
        profitProperty().set(value);
    }

    public MoneyLabel getProfit() {
        return profitProperty().get();
    }

    public ObjectProperty<MoneyLabel> profitPercentageProperty() {
        return profitPercentage;
    }

    public void setProfitPercentage(MoneyLabel value) {
        profitPercentageProperty().set(value);
    }

    public MoneyLabel getProfitPercentage() {
        return profitPercentageProperty().get();
    }

    public ObjectProperty<MoneyLabel> changePercentageProperty() {
        return changePercentage;
    }

    public void setChangePercentage(MoneyLabel value) {
        changePercentageProperty().set(value);
    }

    public MoneyLabel getChangePercentage() {
        return changePercentageProperty().get();
    }

    public ObjectProperty<MoneyLabel> changeProperty() {
        return change;
    }

    public void setChange(MoneyLabel value) {
        changeProperty().set(value);
    }

    public MoneyLabel getChange() {
        return changeProperty().get();
    }

    public ObjectProperty<MoneyLabel> demandProperty() {
        return demand;
    }

    public void setDemand(MoneyLabel value) {
        demandProperty().set(value);
    }

    public MoneyLabel getDemand() {
        return demandProperty().get();
    }
}

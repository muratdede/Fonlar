package org.fon.models;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import org.fon.models.money.MoneyLabel;
import org.fon.models.money.MoneyPercentageLabel;
import org.fon.models.money.MoneyProfitLabel;

public class FonElement {
    private final StringProperty category = new SimpleStringProperty(this, "category");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final IntegerProperty count = new SimpleIntegerProperty(this, "count");
    private final ObjectProperty<MoneyLabel> price = new SimpleObjectProperty<>(this, "price");
    private final ObjectProperty<MoneyLabel> totalPrice = new SimpleObjectProperty<>(this, "totalPrice");
    private final ObjectProperty<MoneyLabel> todayPrice = new SimpleObjectProperty<>(this, "todayPrice");
    private final ObjectProperty<MoneyLabel> totalTodayPrice = new SimpleObjectProperty<>(this, "totalTodayPrice");
    private final ObjectProperty<MoneyLabel> profit = new SimpleObjectProperty<>(this, "profit");
    private final ObjectProperty<MoneyLabel> profitPercentage = new SimpleObjectProperty<>(this, "profitPercentage");
    private final ObjectProperty<MoneyLabel> change = new SimpleObjectProperty<>(this, "change");
    private final ObjectProperty<MoneyLabel> changePercentage = new SimpleObjectProperty<>(this, "changePercentage");
    private final ObjectProperty<MoneyLabel> demand = new SimpleObjectProperty<>(this, "demand");
    private final StringProperty lastUpdate = new SimpleStringProperty(this, "lastUpdate");


    public FonElement (String category, String name, Integer count, Double price, Double todayPrice, Double changePercentage, Double demand, String lastUpdate) {
        this.price.set(new MoneyLabel());
        this.totalPrice.set(new MoneyLabel());
        this.todayPrice.set(new MoneyLabel());
        this.totalTodayPrice.set(new MoneyLabel());
        this.profit.set(new MoneyProfitLabel());
        this.profitPercentage.set(new MoneyPercentageLabel());
        this.changePercentage.set(new MoneyPercentageLabel());
        this.change.set(new MoneyProfitLabel());
        this.demand.set(new MoneyLabel());

        totalPrice.get().valueProperty().bind(Bindings.multiply(this.count, this.price.get().valueProperty()));
        totalTodayPrice.get().valueProperty().bind(Bindings.multiply(this.count, this.todayPrice.get().valueProperty()));
        profit.get().valueProperty().bind(Bindings.subtract(totalTodayPrice.get().valueProperty(), totalPrice.get().valueProperty()));
        profitPercentage.get().valueProperty().bind(Bindings.multiply(Bindings.divide(profit.get().valueProperty(), totalPrice.get().valueProperty()), 100));
        change.get().valueProperty().bind(Bindings.subtract(totalTodayPrice.get().valueProperty(), Bindings.divide(totalTodayPrice.get().valueProperty(), Bindings.add(1, Bindings.divide(this.changePercentage.get().valueProperty(), 100)))));

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

    public ObjectProperty<MoneyLabel> priceProperty() {
        return price;
    }

    public void setPrice(Double value) {
        price.get().setValue(value);
    }

    public Double getPrice() {
        return price.get().getValue();
    }

    public ObjectProperty<MoneyLabel> totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(Double value) {
        totalPrice.get().setValue(value);
    }

    public Double getTotalPrice() {
        return totalPrice.get().getValue();
    }

    public ObjectProperty<MoneyLabel> todayPriceProperty() {
        return todayPrice;
    }

    public void setTodayPrice(Double value) {
        todayPrice.get().setValue(value);
    }

    public Double getTodayPrice() {
        return todayPrice.get().getValue();
    }

    public ObjectProperty<MoneyLabel> totalTodayPriceProperty() {
        return totalTodayPrice;
    }

    public void setTotalTodayPrice(Double value) {
        totalTodayPrice.get().setValue(value);
    }

    public Double getTotalTodayPrice() {
        return totalTodayPrice.get().getValue();
    }

    public ObjectProperty<MoneyLabel> profitProperty() {
        return profit;
    }

    public void setProfit(Double value) {
        profit.get().setValue(value);
    }

    public Double getProfit() {
        return profit.get().getValue();
    }

    public ObjectProperty<MoneyLabel> profitPercentageProperty() {
        return profitPercentage;
    }

    public void setProfitPercentage(Double value) {
        profitPercentage.get().setValue(value);
    }

    public Double getProfitPercentage() {
        return profitPercentage.get().getValue();
    }

    public ObjectProperty<MoneyLabel> changePercentageProperty() {
        return changePercentage;
    }

    public void setChangePercentage(Double value) {
        changePercentage.get().setValue(value);
    }

    public Double getChangePercentage() {
        return changePercentage.get().getValue();
    }

    public ObjectProperty<MoneyLabel> changeProperty() {
        return change;
    }

    public void setChange(Double value) {
        change.get().setValue(value);
    }

    public Double getChange() {
        return change.get().getValue();
    }

    public ObjectProperty<MoneyLabel> demandProperty() {
        return demand;
    }

    public void setDemand(Double value) {
        demand.get().setValue(value);
    }

    public Double getDemand() {
        return demand.get().getValue();
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

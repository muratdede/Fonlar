package org.fon.models.elements;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import org.fon.models.money.MoneyLabel;

public class TransactionElement {
    private final String transactionTableName = "İŞLEM GEÇMİŞİ";
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final IntegerProperty count = new SimpleIntegerProperty(this, "count");
    private final ObjectProperty<MoneyLabel> price = new SimpleObjectProperty<>(this, "price");
    private final ObjectProperty<MoneyLabel> totalPrice = new SimpleObjectProperty<>(this, "totalPrice");
    private final StringProperty transactionTime = new SimpleStringProperty(this, "transactionTime");

    public TransactionElement(String name, Integer count, Double price, String transactionTime) {
        this.name.set(name);
        this.count.set(count);
        this.price.set(new MoneyLabel(price));
        this.totalPrice.set(new MoneyLabel());
        this.transactionTime.set(transactionTime);

        totalPrice.get().valueProperty().bind(Bindings.multiply(this.count, this.price.get().valueProperty()));
    }

    public String getName() {
        return name.get();
    }

    public Integer getCount() {
        return count.get();
    }

    public Double getPrice() {
        return price.get().getValue();
    }

    public Double getTotalPrice() {
        return totalPrice.get().getValue();
    }

    public String getTransactionTime() {
        return transactionTime.get();
    }


    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty countProperty() {
        return count;
    }

    public ObjectProperty<MoneyLabel> priceProperty() {
        return price;
    }

    public ObjectProperty<MoneyLabel> totalPriceProperty() {
        return totalPrice;
    }

    public StringProperty transactionTimeProperty() {
        return transactionTime;
    }

    public String toSqlInsertString() {
        return "INSERT INTO \"" + transactionTableName + "\" (" +
                " \"AD\"" +
                ", \"Adet\"" +
                ", \"Alış/Satış Fiyatı\"" +
                ", \"İşlem Tarihi\") VALUES (" +
                " \"" + getName() + '"' +
                ", \"" + getCount() + '"' +
                ", \"" + getPrice() + '"' +
                ", \"" + getTransactionTime() + "\")";
    }
}

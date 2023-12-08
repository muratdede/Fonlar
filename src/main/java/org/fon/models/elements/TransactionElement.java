package org.fon.models.elements;

import javafx.beans.property.*;
import org.fon.models.money.MoneyLabel;
import org.fon.models.money.MoneyTransactionLabel;

public class TransactionElement {
    private final String transactionTableName = "İŞLEM GEÇMİŞİ";
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final StringProperty type = new SimpleStringProperty(this, "type");
    private final IntegerProperty count = new SimpleIntegerProperty(this, "count");
    private final ObjectProperty<MoneyLabel> price = new SimpleObjectProperty<>(this, "price");
    private final ObjectProperty<MoneyLabel> totalPrice = new SimpleObjectProperty<>(this, "totalPrice");
    private final StringProperty transactionTime = new SimpleStringProperty(this, "transactionTime");

    public TransactionElement(String name, Integer count, Double price, String transactionTime) {
        this.name.set(name);

        String type = "";
        if (count < 0) {
            type = "Satış";
        } else if (count == 0) {
            type = "Talep";
        } else {
            type = "Alış";
        }
        this.type.set(type);

        this.count.set(Math.abs(count));
        this.price.set(new MoneyTransactionLabel(price * (count / Math.abs(count))));
        this.totalPrice.set(new MoneyTransactionLabel(count * price));
        this.transactionTime.set(transactionTime);
    }

    public String getName() {
        return name.get();
    }
    
    public String getType() {
        return type.get();
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

    public StringProperty typeProperty() {
        return type;
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

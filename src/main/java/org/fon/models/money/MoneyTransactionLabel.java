package org.fon.models.money;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;

public class MoneyTransactionLabel extends MoneyLabel {

    public MoneyTransactionLabel() {
        createColorBinding();
    }

    public MoneyTransactionLabel(Double initialValue) {
        super(initialValue);

        createColorBinding();
        overrideTextBinding();
    }

    private void overrideTextBinding() {
        textProperty().unbind();

        textProperty().bind(Bindings.createStringBinding(() -> formatter.format(Math.abs(valueProperty.get())), valueProperty));
    }

    private void createColorBinding() {
        textFillProperty().bind(Bindings.when(Bindings.lessThan(valueProperty, 0))
                .then(Color.GREEN)
                .otherwise(Color.RED));
    }
}

package org.fon.models;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;

public class MoneyProfitLabel extends MoneyLabel{

    public MoneyProfitLabel() {
        createColorBinding();
    }

    private void createColorBinding() {
        textFillProperty().bind(Bindings.when(Bindings.lessThan(valueProperty, 0))
                .then(Color.RED)
                .otherwise(Color.GREEN));
    }
}

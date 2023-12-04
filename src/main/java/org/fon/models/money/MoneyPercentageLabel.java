package org.fon.models.money;

import javafx.beans.binding.Bindings;

public class MoneyPercentageLabel extends MoneyProfitLabel{

    public MoneyPercentageLabel() {
        createPercentageBinding();
    }

    private void createPercentageBinding() {
        textProperty().unbind();

        textProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = valueProperty.get();
            if (Double.isNaN(percentage)) {
                return "%0,00";
            } else if (percentage < 0) {
                return "-%" + formatter.format(-percentage);
            } else {
                return "%" + formatter.format(percentage);
            }
        }, valueProperty));
    }
}

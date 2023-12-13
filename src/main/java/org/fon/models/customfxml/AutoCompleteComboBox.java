package org.fon.models.customfxml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBox extends ComboBox<String> {

    private ObservableList<String> autoCompleteItems = null;
    private int lastCaretPos = 0;

    public AutoCompleteComboBox() {
        super();
        initAutoComplete();
        setEditable(true);
    }

    private void initAutoComplete() {
        addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            String str = getEditor().getText();
            boolean isHide = false;

            switch (event.getCode()) {
                case UP:
                case DOWN:
                    return;
                case DELETE:
                case BACK_SPACE:
                    if (str == null)
                        break;

                    getSelectionModel().clearSelection();
                    getEditor().setText(str);
                    getEditor().positionCaret(str.length());
                    break;
                case ENTER:
                    getEditor().positionCaret(str.length());
                case ESCAPE:
                    isHide = true;
                case LEFT:
                case RIGHT:
                    break;
            }

            lastCaretPos = getEditor().getCaretPosition();
            if (lastCaretPos == 0) {
                hide();
                return;
            }

            ObservableList<String> newItems = FXCollections.observableArrayList();
            autoCompleteItems.forEach(item -> {
                if (item.substring(0, Math.min(lastCaretPos, item.length())).equalsIgnoreCase(this.getEditor().getText().substring(0, lastCaretPos))) {
                    newItems.add(item);
                }
            });

            setItems(newItems);
            getEditor().positionCaret(lastCaretPos);

            if (newItems.size() == 1)
                getSelectionModel().selectFirst();

            if (isHide)
                hide();
            else
                show();
        });

        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.equalsIgnoreCase(newValue))
                return;

            for (String item : autoCompleteItems) {
                if (item.contains(newValue.toUpperCase())) {
                    getEditor().setText(newValue.toUpperCase());
                    return;
                }
            }

            getEditor().setText(oldValue);
        });
    }

    public void setAutoCompleteItems(ObservableList<String> items) {
        autoCompleteItems = items.sorted();
    }
}
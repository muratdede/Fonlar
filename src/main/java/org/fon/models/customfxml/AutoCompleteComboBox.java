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
            String str = getSelectionModel().getSelectedItem();

            switch (event.getCode()) {
                case ESCAPE:
                    hide();
                case UP:
                case DOWN:
                    return;
                case ENTER:
                    if (str == null)
                        return;

                    getSelectionModel().clearSelection();
                    getEditor().setText(str);
                    getEditor().positionCaret(str.length());
                    break;
                case DELETE:
                case BACK_SPACE:
                    if (str == null) {
                        break;
                    }

                    getSelectionModel().clearSelection();
                    getEditor().setText(str);
                    break;
                case LEFT:
                case RIGHT:
                    break;
            }

            lastCaretPos = getEditor().getCaretPosition();

            if (lastCaretPos == 0) {
                if (isShowing())
                    hide();
                setItems(autoCompleteItems);
                return;
            }

            ObservableList<String> newItems = FXCollections.observableArrayList();
            autoCompleteItems.forEach(item -> {
                if (item.substring(0, Math.min(lastCaretPos, item.length())).equalsIgnoreCase(this.getEditor().getText().substring(0, lastCaretPos))) {
                    newItems.add(item);
                }
            });

            if (newItems.isEmpty()) {
                getEditor().setStyle("-fx-text-fill: red;");
            } else {
                getEditor().setStyle("-fx-text-fill: black;");
            }

            setItems(newItems);
            getEditor().positionCaret(lastCaretPos);

            hide();
            show();
        });
    }

    public void setAutoCompleteItems(ObservableList<String> items) {
        autoCompleteItems = items.sorted();
    }
}
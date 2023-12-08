package org.fon.presentation.transactions;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fon.handlers.DatabaseHandler;
import org.fon.models.IButtonPage;
import org.fon.models.elements.TransactionElement;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsPresenter implements Initializable, IButtonPage {
    @Inject
    DatabaseHandler databaseHandler;

    @FXML
    private TableColumn<TransactionElement, String> name;
    @FXML
    private TableColumn<TransactionElement, String> type;
    @FXML
    private TableColumn<TransactionElement, String> count;
    @FXML
    private TableColumn<TransactionElement, String> price;
    @FXML
    private TableColumn<TransactionElement, String> totalPrice;
    @FXML
    private TableColumn<TransactionElement, String> time;
    @FXML
    private TableView<TransactionElement> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        count.setCellValueFactory(new PropertyValueFactory<>("count"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        time.setCellValueFactory(new PropertyValueFactory<>("transactionTime"));

        table.setItems(databaseHandler.getTransactions());
    }

}


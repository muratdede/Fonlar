package org.fon.presentation.islemGecmisi;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.fon.handlers.DatabaseHandler;
import org.fon.models.elements.FonElement;
import org.fon.models.IButtonPage;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class IslemGecmisiPresenter implements Initializable, IButtonPage {
    @Inject
    DatabaseHandler databaseHandler;

    @FXML
    private TableColumn<FonElement, String> name;
    @FXML
    private TableColumn<FonElement, String> count;
    @FXML
    private TableColumn<FonElement, String> price;
    @FXML
    private TableColumn<FonElement, String> totalPrice;
    @FXML
    private TableColumn<FonElement, String> demand;
    @FXML
    private TableColumn<FonElement, String> time;
    @FXML
    private TableView<FonElement> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        count.setCellValueFactory(new PropertyValueFactory<>("count"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        demand.setCellValueFactory(new PropertyValueFactory<>("demand"));

        table.setItems(databaseHandler.getTransactions());
    }

}


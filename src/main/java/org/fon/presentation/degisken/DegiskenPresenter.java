package org.fon.presentation.degisken;


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

public class DegiskenPresenter implements Initializable, IButtonPage {
    @Inject
    DatabaseHandler databaseHandler;

    @FXML
    public TableColumn<FonElement, String> name;
    @FXML
    public TableColumn<FonElement, String> count;
    @FXML
    public TableColumn<FonElement, String> price;
    @FXML
    public TableColumn<FonElement, String> totalPrice;
    @FXML
    public TableColumn<FonElement, String> todayPrice;
    @FXML
    public TableColumn<FonElement, String> totalTodayPrice;
    @FXML
    public TableColumn<FonElement, String> profit;
    @FXML
    public TableColumn<FonElement, String> profitPercentage;
    @FXML
    public TableColumn<FonElement, String> changePercentage;
    @FXML
    public TableColumn<FonElement, String> change;
    @FXML
    public TableColumn<FonElement, String> demand;
    @FXML
    public TableColumn<FonElement, String> lastUpdate;
    @FXML
    public TableView<FonElement> table;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        count.setCellValueFactory(new PropertyValueFactory<>("count"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        todayPrice.setCellValueFactory(new PropertyValueFactory<>("todayPrice"));
        totalTodayPrice.setCellValueFactory(new PropertyValueFactory<>("totalTodayPrice"));
        profit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        profitPercentage.setCellValueFactory(new PropertyValueFactory<>("profitPercentage"));
        changePercentage.setCellValueFactory(new PropertyValueFactory<>("changePercentage"));
        change.setCellValueFactory(new PropertyValueFactory<>("change"));
        demand.setCellValueFactory(new PropertyValueFactory<>("demand"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));

        table.setItems(databaseHandler.getFonList("DEĞİŞKEN"));
    }

}


package org.fon.models.customfxml.foncontroller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fon.App;
import org.fon.handlers.GraphData;
import org.fon.handlers.TefasParser;
import org.fon.models.elements.FonElement;
import org.fon.presentation.stockChart.StockChartPresenter;
import org.fon.presentation.stockChart.StockChartView;

import java.net.URL;
import java.util.ResourceBundle;

public class FonController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private TableColumn<FonElement, String> name;
    @FXML
    private TableColumn<FonElement, String> count;
    @FXML
    private TableColumn<FonElement, String> price;
    @FXML
    private TableColumn<FonElement, String> totalPrice;
    @FXML
    private TableColumn<FonElement, String> todayPrice;
    @FXML
    private TableColumn<FonElement, String> totalTodayPrice;
    @FXML
    private TableColumn<FonElement, String> profit;
    @FXML
    private TableColumn<FonElement, String> profitPercentage;
    @FXML
    private TableColumn<FonElement, String> changePercentage;
    @FXML
    private TableColumn<FonElement, String> change;
    @FXML
    private TableColumn<FonElement, String> demand;
    @FXML
    private TableColumn<FonElement, String> lastUpdate;
    @FXML
    private TableView<FonElement> table;

    public void setItems(ObservableList<FonElement> items) {
        table.setItems(items);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(element -> element.getValue().nameProperty());
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
        lastUpdate.setCellValueFactory(element -> element.getValue().lastUpdateProperty());
    }

    @FXML
    public void onMouseDoubleCliked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (!(mouseEvent.getSource() instanceof TableView)) {
                throw new RuntimeException("illegal mouse event call");
            }

            Object source = ((TableView<?>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();

            if (!(source instanceof FonElement)) {
                throw new RuntimeException("illegal table element class");
            }

            FonElement fonElement = (FonElement) source;

            StockChartView stockView = new StockChartView();
            Scene stockScene = new Scene(stockView.getView());

            GraphData graphData = TefasParser.parseGraphData(fonElement.getName());

            ((StockChartPresenter) stockView.getPresenter()).initializeTable(graphData.getSeries());

            Stage stockStage = new Stage();
            stockStage.initOwner(App.stage);
            stockStage.setScene(stockScene);
            stockStage.setTitle(graphData.getLongFonName());
            stockStage.show();
        }
    }
}

package org.fon.models.customfxml.foncontroller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

import java.io.IOException;
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

    /*
    TODO:
    Extract This Class
    */
    static class MyTableRow extends TableRow<FonElement> {
        MyTableRow() {
            setOnMouseMoved(mouseEvent -> updateCursor(mouseEvent.isControlDown()));
            setOnMouseExited(mouseEvent -> getScene().setCursor(Cursor.DEFAULT));

            setOnMouseClicked(this::handleOnMouseClicked);
        }

        private void handleOnMouseClicked(MouseEvent mouseEvent) {
            if (mouseEvent.getClickCount() == 1 && mouseEvent.isControlDown()) {
                try {
                    Runtime.getRuntime().exec("cmd /c start " + "https://www.tefas.gov.tr/tr/fon-detayli-analiz/" + getItem().getName());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (mouseEvent.getClickCount() == 2) {

                StockChartView stockView = new StockChartView();
                Scene stockScene = new Scene(stockView.getView());

                GraphData graphData = TefasParser.parseGraphData(getItem().getName());

                ((StockChartPresenter) stockView.getPresenter()).initializeTable(graphData.getSeries());

                Stage stockStage = new Stage();
                stockStage.initOwner(App.stage);
                stockStage.setScene(stockScene);
                stockStage.setTitle(graphData.getLongFonName());
                stockStage.show();
            }
        }

        void updateCursor(boolean isControlDown) {
            if (isControlDown && (getItem() != null) && !getItem().getName().isEmpty()) {
                getScene().setCursor(Cursor.HAND);
            } else {
                getScene().setCursor(Cursor.DEFAULT);
            }
        }
    }

    public void setItems(ObservableList<FonElement> items) {
        table.setRowFactory(param -> new MyTableRow());
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
}

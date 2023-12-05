package org.fon.models;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.fon.handlers.GraphData;
import org.fon.handlers.TefasParser;
import org.fon.models.elements.FonElement;
import org.fon.presentation.stockChart.StockClassController;
import org.fon.presentation.stockChart.StockClassView;

public class FonPresenter {

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

            StockClassView stockView = new StockClassView();
            Scene stockScene = new Scene(stockView.getView());

            GraphData graphData = TefasParser.parseGraphData(fonElement.getName());

            ((StockClassController) stockView.getPresenter()).initializeTable(graphData.getSeries());

            Stage stockStage = new Stage();
            stockStage.setScene(stockScene);
            stockStage.setTitle(graphData.getLongFonName());
            stockStage.show();
        }
    }
}

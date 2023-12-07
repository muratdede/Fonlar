package org.fon.presentation.total;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.fon.handlers.DatabaseHandler;
import org.fon.models.elements.CategoryElement;
import org.fon.models.elements.FonElement;
import org.fon.models.IButtonPage;
import org.fon.models.elements.TotalElement;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import org.fon.App;

public class TotalPresenter implements Initializable, IButtonPage {
    @Inject
    DatabaseHandler databaseHandler;

    @FXML
    private TableColumn<CategoryElement, String> category;
    @FXML
    private TableColumn<CategoryElement, String> totalPrice;
    @FXML
    private TableColumn<CategoryElement, String> todayTotalPrice;
    @FXML
    private TableColumn<CategoryElement, String> profit;
    @FXML
    private TableColumn<CategoryElement, String> profitPercentage;
    @FXML
    private TableColumn<CategoryElement, String> changePercentage;
    @FXML
    private TableColumn<CategoryElement, String> change;
    @FXML
    private TableColumn<CategoryElement, String> demand;
    @FXML
    private TableView<CategoryElement> table;

    @FXML
    private TableColumn<TotalElement, String> category1;
    @FXML
    private TableColumn<TotalElement, String> totalPrice1;
    @FXML
    private TableColumn<TotalElement, String> todayTotalPrice1;
    @FXML
    private TableColumn<TotalElement, String> profit1;
    @FXML
    private TableColumn<TotalElement, String> profitPercentage1;
    @FXML
    private TableColumn<TotalElement, String> changePercentage1;
    @FXML
    private TableColumn<TotalElement, String> change1;
    @FXML
    private TableColumn<TotalElement, String> demand1;
    @FXML
    private TableView<TotalElement> table1;

    @FXML
    public ComboBox<String> categoryComboBox;
    @FXML
    public TextField nameTextField;
    @FXML
    public TextField countTextField;
    @FXML
    public TextField priceTextField;
    @FXML
    public TextField demandTextField;
    @FXML
    public Button ekleButton;
    @FXML
    public Button updateButton;

    @FXML
    private PieChart pieChart;

    private final ObservableList<CategoryElement> categoryElements = FXCollections.observableArrayList();
    private final ObservableList<TotalElement> totalElements = FXCollections.observableArrayList();
    private final ObservableList<PieChart.Data> pieElements = FXCollections.observableArrayList();

    private Pane loadingPane;

    @Override
    public void keyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.getKeyCode("F5"))
            updateButton.fire();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("HİSSE FONLARI"), "HİSSE FONLARI"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("KIYMETLİ MADENLER"), "KIYMETLİ MADENLER"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("DEĞİŞKEN"), "DEĞİŞKEN"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("KARMA"), "KARMA"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("FON SEPETİ"), "FON SEPETİ"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("KATILIM"), "KATILIM"));
        categoryElements.add(new CategoryElement(databaseHandler.getFonList("PARA PİYASASI"), "PARA PİYASASI"));

        totalElements.add(new TotalElement(categoryElements));
        totalElements.forEach(TotalElement::update);

        categoryElements.forEach(categoryElement -> {
            categoryElement.update();

            PieChart.Data pieChartData = new PieChart.Data(categoryElement.categoryValueProperty().get(), 0);
            pieChartData.pieValueProperty().bind(Bindings.multiply(100, Bindings.divide(categoryElement.todayTotalPriceValueProperty(), totalElements.get(0).todayTotalPriceValueProperty())));
            pieChartData.getNode();

            pieElements.add(pieChartData);
        });

        pieChart.setData(pieElements);

        Tooltip tooltip = new Tooltip();
        pieChart.getData().forEach(pieData -> {
            pieData.getNode().setOnMouseEntered(mouseEvent -> {
                tooltip.setText(String.format("%s\n%%%.2f", pieData.getName(), pieData.getPieValue()));
                tooltip.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");

                tooltip.show(pieData.getNode(), mouseEvent.getScreenX() + 15, mouseEvent.getScreenY() + 15);
            });
            pieData.getNode().setOnMouseMoved(mouseEvent -> {
                tooltip.setAnchorX(mouseEvent.getScreenX() + 15);
                tooltip.setAnchorY(mouseEvent.getScreenY() + 15);
            });
            pieData.getNode().setOnMouseExited(mouseEvent -> tooltip.hide());
        });

        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        todayTotalPrice.setCellValueFactory(new PropertyValueFactory<>("todayTotalPrice"));
        profit.setCellValueFactory(new PropertyValueFactory<>("profit"));
        profitPercentage.setCellValueFactory(new PropertyValueFactory<>("profitPercentage"));
        changePercentage.setCellValueFactory(new PropertyValueFactory<>("changePercentage"));
        change.setCellValueFactory(new PropertyValueFactory<>("change"));
        demand.setCellValueFactory(new PropertyValueFactory<>("demand"));
        table.setItems(categoryElements);

        category1.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalPrice1.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        todayTotalPrice1.setCellValueFactory(new PropertyValueFactory<>("todayTotalPrice"));
        profit1.setCellValueFactory(new PropertyValueFactory<>("profit"));
        profitPercentage1.setCellValueFactory(new PropertyValueFactory<>("profitPercentage"));
        changePercentage1.setCellValueFactory(new PropertyValueFactory<>("changePercentage"));
        change1.setCellValueFactory(new PropertyValueFactory<>("change"));
        demand1.setCellValueFactory(new PropertyValueFactory<>("demand"));
        table1.setItems(totalElements);

        categoryComboBox.setItems(databaseHandler.getFonCategories());
        categoryComboBox.getSelectionModel().selectFirst();

        ekleButton.disableProperty().bind(Bindings.createBooleanBinding(
            ()-> {
                try {
                    Integer.parseInt(countTextField.getText());
                    if (Double.parseDouble(priceTextField.getText()) < 0)
                        return false;
                    if (Double.parseDouble(demandTextField.getText()) < 0)
                        return false;
                } catch (Exception e) {
                    return true;
                }
                return nameTextField.getText().isEmpty();
            },
            nameTextField.textProperty(),
            countTextField.textProperty(),
            priceTextField.textProperty(),
            demandTextField.textProperty()
        ));

        createPopUp();
    }

    public void updatePrices(ActionEvent ignored) {
        updateButton.setDisable(true);
        App.showPopup(loadingPane);
        new Thread(()-> {
            databaseHandler.updatePrices();
            Platform.runLater(() -> {
                updateButton.setDisable(false);
                App.showPopup(null);
            });
        }).start();
    }

    public void newEntry(ActionEvent ignored) {
        FonElement fonElement = new FonElement(categoryComboBox.getSelectionModel().getSelectedItem(),
                nameTextField.getText().toUpperCase(),
                Integer.parseInt(countTextField.getText()),
                Double.parseDouble(priceTextField.getText()),
                0.0,
                0.0,
                Double.parseDouble(demandTextField.getText()),
                "");

        databaseHandler.addFon(fonElement);
    }

    private void createPopUp() {
        // create outer vBox pane
        VBox vBox1 = new VBox();
        ProgressIndicator progressIndicator = new ProgressIndicator(-1);
        progressIndicator.setStyle("-fx-accent: #AAAAAA;");

        vBox1.getChildren().add(progressIndicator);

        // create a popup
        loadingPane = new Pane();

        // add the label
        loadingPane.getChildren().add(vBox1);

        // set size of labels

        vBox1.setMaxHeight(200);
        vBox1.setMaxWidth(200);
        vBox1.setPrefHeight(200);
        vBox1.setPrefWidth(200);
        loadingPane.setMaxHeight(200);
        loadingPane.setMaxWidth(200);
        loadingPane.setPrefHeight(200);
        loadingPane.setPrefWidth(200);
    }
}


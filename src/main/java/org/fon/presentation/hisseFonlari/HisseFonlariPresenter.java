package org.fon.presentation.hisseFonlari;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fon.handlers.DatabaseHandler;
import org.fon.models.IButtonPage;
import org.fon.models.customfxml.foncontroller.FonController;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class HisseFonlariPresenter implements Initializable, IButtonPage {
    @Inject
    DatabaseHandler databaseHandler;

    @FXML
    private FonController fonController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fonController.setItems(databaseHandler.getFonList("HİSSE FONLARI"));
    }

}


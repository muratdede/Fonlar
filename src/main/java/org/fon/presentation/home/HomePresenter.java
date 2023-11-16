package org.fon.presentation.home;


import com.airhacks.afterburner.views.FXMLView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.fon.models.IButtonPage;
import org.fon.presentation.degisken.DegiskenView;
import org.fon.presentation.fonSepeti.FonSepetiView;
import org.fon.presentation.hisseFonlari.HisseFonlariView;
import org.fon.presentation.islemGecmisi.IslemGecmisiView;
import org.fon.presentation.karma.KarmaView;
import org.fon.presentation.katilim.KatilimView;
import org.fon.presentation.kiymetliMadenler.KiymetliMadenlerView;
import org.fon.presentation.paraPiyasasi.ParaPiyasasiView;
import org.fon.presentation.total.TotalView;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class HomePresenter implements Initializable, IButtonPage {
    @FXML
    public BorderPane root;
    @FXML
    private AnchorPane content;
    @FXML
    private Button total;
    @FXML
    private Button hisseFonlari;
    @FXML
    private Button kiymetliMadenler;
    @FXML
    private Button degisken;
    @FXML
    private Button karma;
    @FXML
    private Button fonSepeti;
    @FXML
    private Button katilim;
    @FXML
    private Button paraPiyasasi;
    @FXML
    private Button islemGecmisi;

    private HashMap<Button, FXMLView> buttonFXMLViewHashMap = new HashMap<>();
    private IButtonPage currentButtonPage = null;
    private IButtonPage totalButtonPage = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonFXMLViewHashMap.put(total, new TotalView());
        buttonFXMLViewHashMap.put(hisseFonlari, new HisseFonlariView());
        buttonFXMLViewHashMap.put(kiymetliMadenler, new KiymetliMadenlerView());
        buttonFXMLViewHashMap.put(degisken, new DegiskenView());
        buttonFXMLViewHashMap.put(karma, new KarmaView());
        buttonFXMLViewHashMap.put(fonSepeti, new FonSepetiView());
        buttonFXMLViewHashMap.put(katilim, new KatilimView());
        buttonFXMLViewHashMap.put(paraPiyasasi, new ParaPiyasasiView());
        buttonFXMLViewHashMap.put(islemGecmisi, new IslemGecmisiView());

        switchPage(total);

        totalButtonPage = (IButtonPage) (buttonFXMLViewHashMap.get(total)).getPresenter();

        root.setOnKeyPressed(keyEvent -> {
            if (currentButtonPage == null)
                return;
            
            KeyCode keyCode = keyEvent.getCode();
            
            if (keyCode == KeyCode.getKeyCode("F5"))
                totalButtonPage.keyPressed(keyCode);
            
            currentButtonPage.keyPressed(keyCode);
        });
    }

    public void buttonPressed(ActionEvent event) {
        Button btn = (Button) event.getSource();
        
        switchPage(btn);
    }
    
    public void switchPage(Button btn) {
        FXMLView fxmlView = buttonFXMLViewHashMap.getOrDefault(btn, null);
        if (fxmlView == null)
            return;

        resetButtonColors();
        selectButtonColor(btn);

        currentButtonPage = (IButtonPage) fxmlView.getPresenter();
        
        content.getChildren().clear();
        content.getChildren().add(fxmlView.getView());
    }

    private void resetButtonColors() {
        for (Button button : buttonFXMLViewHashMap.keySet()) {
            button.getStyleClass().remove("selected-button1");
            button.getStyleClass().add("button1");
        }
    }

    private void selectButtonColor(Button button) {
        button.getStyleClass().remove("button1");
        button.getStyleClass().add("selected-button1");
    }
}


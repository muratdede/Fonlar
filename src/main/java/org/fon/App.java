package org.fon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.fon.handlers.SSLDisabler;
import org.fon.presentation.home.HomePresenter;
import org.fon.presentation.home.HomeView;

import java.util.Locale;

public class App extends Application {
    private static HomePresenter homePresenter = null;
    public static Stage stage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        Locale.setDefault(new Locale("tr", "TR"));

        stage = primaryStage;

        Platform.setImplicitExit(true);

        HomeView homeView = new HomeView();
        Scene scene = new Scene(homeView.getView());
        homePresenter = (HomePresenter) homeView.getPresenter();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tefas Fon Yardımcısı");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> Platform.exit());

        primaryStage.show();
    }

    @Override
    public void stop() {

    }

    public static void showPopup(Pane pane) {
        homePresenter.showPopup(pane);
    }
}
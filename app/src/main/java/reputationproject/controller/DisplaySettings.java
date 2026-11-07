package reputationproject.controller;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import reputationproject.model.Resolution;
import reputationproject.model.Theme;
import reputationproject.respository.AppState;

public class DisplaySettings {
    private static final String BASE_STYLESHEET = "/view/app_style.css";

    private DisplaySettings() {
    }

    public static void apply(Stage stage) {
        AppState state = AppState.getInstance();
        applyTheme(stage.getScene(), state.getTheme());
        applyResolution(stage, state.getResolution());
    }

    public static void applyTheme(Scene scene, Theme theme) {
        scene.getStylesheets().setAll(
                DisplaySettings.class.getResource(BASE_STYLESHEET).toExternalForm(),
                DisplaySettings.class.getResource(theme.getStylesheet()).toExternalForm());
    }

    public static void applyResolution(Stage stage, Resolution resolution) {
        Region root = (Region) stage.getScene().getRoot();
        root.setPrefSize(resolution.getWidth(), resolution.getHeight());
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}

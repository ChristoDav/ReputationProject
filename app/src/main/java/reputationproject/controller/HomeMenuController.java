package reputationproject.controller;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class HomeMenuController {
    @FXML
    private Pane sidebar;

    private boolean isOpen = false;

    @FXML
    private void toggleSidebar() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(250), sidebar);

        if (isOpen) {
            tt.setToX(-200);
        } else {
            tt.setToX(0);
        }

        isOpen = !isOpen;
        tt.play();
    }

    @FXML
    private void handleCharacters() {
        System.out.println("Character menu clicked");
    }

    @FXML
    private void handleSettings() {
        System.out.println("Setting menu clicked");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

}
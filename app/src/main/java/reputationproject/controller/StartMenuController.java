package reputationproject.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class StartMenuController {

    @FXML
    private void handleStart() {
        System.out.println("Start clicked");
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}
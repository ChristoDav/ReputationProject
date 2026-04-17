package reputationproject.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HomeMenuController {
    // TODO: Pass to use constants over magic numbers
    private final double SIDEBAR_TRANSITION_DURATION = 250;

    @FXML
    private StackPane startMenuStackPane;

    @FXML
    private SplitPane startMenu;

    @FXML
    private Pane startMenuOverlay;

    @FXML
    private Pane mainContent;

    @FXML
    private VBox sidebar;

    @FXML
    private Pane sidebarOverlay;

    private GaussianBlur blur;

    Timeline blurAnimation;

    Timeline blurAnimationUndo;

    TranslateTransition ttSidebar;

    FadeTransition ftSidebarOverlay;

    private boolean isSidebarOpen;

    @FXML
    private void initialize() {
        isSidebarOpen = false;

        blur = new GaussianBlur(10);

        ttSidebar = new TranslateTransition(Duration.millis(SIDEBAR_TRANSITION_DURATION), sidebar);

        ftSidebarOverlay = new FadeTransition(Duration.millis(SIDEBAR_TRANSITION_DURATION),
                sidebarOverlay);

        blurAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 0)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(blur.radiusProperty(), 10)));

        blurAnimationUndo = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(blur.radiusProperty(), 10)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(blur.radiusProperty(), 0)));

        mainContent.setEffect(blur);
    }

    // Start menu button functions

    @FXML
    private void handleStartNewWorld() {
        System.out.println("Start New World clicked");
        undarkenMainContentPane();
        blurAnimationUndo.play();
        startMenuStackPane.setMouseTransparent(true);
        startMenuStackPane.setVisible(false);
        startMenuStackPane.setDisable(true);
    }

    @FXML
    private void handleOpenExistingWorld() {
        System.out.println("Open Existing World clicked");
    }

    // Sidebar button functions

    @FXML
    private void handleSidebar() {
        if (isSidebarOpen) {
            closeSidebar();
            undarkenMainContentPane();
            blurAnimationUndo.play();
        } else {
            openSidebar();
            darkenMainContentPane();
            blurAnimation.play();
        }
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
    private void handleReturnToWorldMenu() {
        System.out.println("Return to World Menu clicked");
        closeSidebar();
        startMenuStackPane.setMouseTransparent(false);
        startMenuStackPane.setVisible(true);
        startMenuStackPane.setDisable(false);
    }

    // Universal button functions

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    // Utility functions

    private void closeSidebar() {
        ttSidebar.setToX(-200);
        ttSidebar.play();
        sidebar.setMouseTransparent(true);
        sidebarOverlay.setMouseTransparent(true);
        isSidebarOpen = false;        
    }

    private void openSidebar() {
        ttSidebar.setToX(0);
        ttSidebar.play();
        sidebar.setMouseTransparent(false);
        sidebarOverlay.setMouseTransparent(false);
        isSidebarOpen = true;           
    }

    private void darkenMainContentPane() {
        ftSidebarOverlay.setFromValue(0);
        ftSidebarOverlay.setToValue(0.5);
        ftSidebarOverlay.play();
    }

    private void undarkenMainContentPane() {
        ftSidebarOverlay.setFromValue(0.5);
        ftSidebarOverlay.setToValue(0);        
        ftSidebarOverlay.play();
    }
}
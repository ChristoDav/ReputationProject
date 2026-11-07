package reputationproject.controller;

import java.sql.SQLException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import reputationproject.model.Page;
import reputationproject.model.World;
import reputationproject.respository.AppState;

public class HomeMenuController {
    // TODO: Pass to use constants over magic numbers
    private final double SIDEBAR_TRANSITION_DURATION = 250;
    private final double RECENT_PAGE_BUTTON_WIDTH = 200;
    private final double RECENT_PAGE_BUTTON_HEIGHT = 40;
    private final String APP_TITLE = "Reputation Project";

    @FXML
    private StackPane startMenuStackPane;

    @FXML
    private SplitPane startMenu;

    @FXML
    private Pane startMenuOverlay;

    @FXML
    private VBox newWorldPanel;

    @FXML
    private TextField newWorldName;

    @FXML
    private VBox openWorldPanel;

    @FXML
    private ListView<World> worldList;

    @FXML
    private Button openWorldButton;

    @FXML
    private Text worldMenuMessage;

    @FXML
    private Pane mainContent;

    @FXML
    private Text pageTitle;

    @FXML
    private Text pageSubtitle;

    @FXML
    private HBox homePage;

    @FXML
    private VBox recentPagesList;

    @FXML
    private Parent actorsPage;

    @FXML
    private Parent settingsPage;

    @FXML
    private SettingsMenuController settingsPageController;

    @FXML
    private VBox sidebar;

    @FXML
    private Pane sidebarOverlay;

    private AppState state;

    private GaussianBlur blur;

    Timeline blurAnimation;

    Timeline blurAnimationUndo;

    TranslateTransition ttSidebar;

    FadeTransition ftSidebarOverlay;

    private boolean isSidebarOpen;

    @FXML
    private void initialize() {
        state = AppState.getInstance();

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

        openWorldButton.disableProperty().bind(worldList.getSelectionModel().selectedItemProperty().isNull());

        settingsPageController.setOnDatabaseReplaced(this::handleDatabaseReplaced);

        showStartMenu();
    }

    // Start menu button functions

    @FXML
    private void handleStartNewWorld() {
        System.out.println("Start New World clicked");
        showWorldPanel(newWorldPanel);
    }

    @FXML
    private void handleOpenExistingWorld() {
        System.out.println("Open Existing World clicked");
        showWorldPanel(openWorldPanel);
    }

    @FXML
    private void handleCreateWorld() {
        System.out.println("Create World clicked");
        String name = newWorldName.getText().trim();
        if (name.isEmpty()) {
            worldMenuMessage.setText("Enter a name for the new world.");
            return;
        }
        if (state.hasWorld(name)) {
            worldMenuMessage.setText("A world named " + name + " already exists.");
            return;
        }

        try {
            state.createWorld(name);
            enterWorld();
        } catch (SQLException e) {
            worldMenuMessage.setText("The world could not be created: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenWorld() {
        System.out.println("Open World clicked");
        try {
            state.openWorld(worldList.getSelectionModel().getSelectedItem());
            enterWorld();
        } catch (SQLException e) {
            worldMenuMessage.setText("The world could not be opened: " + e.getMessage());
        }
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
    private void handleHome() {
        System.out.println("Home menu clicked");
        openPage(Page.HOME);
    }

    @FXML
    private void handleActors() {
        System.out.println("Actor menu clicked");
        openPage(Page.ACTORS);
    }

    @FXML
    private void handleSettings() {
        System.out.println("Setting menu clicked");
        openPage(Page.SETTINGS);
    }

    @FXML
    private void handleReturnToWorldMenu() {
        System.out.println("Return to World Menu clicked");
        closeSidebar();
        state.closeWorld();
        showStartMenu();
    }

    // Universal button functions

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    // Utility functions

    private void handleDatabaseReplaced() {
        darkenMainContentPane();
        blurAnimation.play();
        showStartMenu();
    }

    private void enterWorld() {
        undarkenMainContentPane();
        blurAnimationUndo.play();
        startMenuStackPane.setMouseTransparent(true);
        startMenuStackPane.setVisible(false);
        startMenuStackPane.setDisable(true);
        showPage(Page.HOME);
        refreshRecentPages();
    }

    private void showStartMenu() {
        startMenuStackPane.setMouseTransparent(false);
        startMenuStackPane.setVisible(true);
        startMenuStackPane.setDisable(false);
        newWorldName.clear();
        worldList.getItems().setAll(state.getWorlds());
        worldList.getSelectionModel().selectFirst();
        showWorldPanel(state.getWorlds().isEmpty() ? newWorldPanel : openWorldPanel);
        showPage(Page.HOME);
        refreshRecentPages();
    }

    private void showWorldPanel(VBox panel) {
        newWorldPanel.setVisible(panel == newWorldPanel);
        openWorldPanel.setVisible(panel == openWorldPanel);
        worldMenuMessage.setText("");
    }

    private void openPage(Page page) {
        if (isSidebarOpen) {
            handleSidebar();
        }
        showPage(page);

        if (page != Page.HOME) {
            try {
                state.recordPageVisit(page);
            } catch (SQLException e) {
                System.out.println("Page visit not saved: " + e.getMessage());
            }
        }
        refreshRecentPages();
    }

    private void showPage(Page page) {
        homePage.setVisible(page == Page.HOME);
        actorsPage.setVisible(page == Page.ACTORS);
        settingsPage.setVisible(page == Page.SETTINGS);

        pageTitle.setText(page == Page.HOME ? APP_TITLE : page.toString());

        World world = state.getCurrentWorld();
        if (world == null) {
            pageSubtitle.setText("");
        } else {
            pageSubtitle.setText(page == Page.HOME ? "Welcome to " + world.getName() : world.getName());
        }

        if (page == Page.SETTINGS) {
            settingsPageController.refresh();
        }
    }

    private void refreshRecentPages() {
        recentPagesList.getChildren().clear();

        if (state.getRecentPages().isEmpty()) {
            recentPagesList.getChildren().add(new Text("No pages accessed yet"));
        }

        for (Page page : state.getRecentPages()) {
            Button button = new Button(page.toString());
            button.setMnemonicParsing(false);
            button.setPrefSize(RECENT_PAGE_BUTTON_WIDTH, RECENT_PAGE_BUTTON_HEIGHT);
            button.setOnAction(event -> openPage(page));
            recentPagesList.getChildren().add(button);
        }
    }

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

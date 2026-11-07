package reputationproject.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import reputationproject.model.Resolution;
import reputationproject.model.Theme;
import reputationproject.respository.AppState;

public class SettingsMenuController {
    private final String WIPE_CONFIRMATION_WORD = "WIPE";
    private final String EXPORT_FILE_NAME = "reputation_export.db";

    @FXML
    private VBox settingsMenu;

    @FXML
    private ComboBox<Resolution> resolutionChoice;

    @FXML
    private ComboBox<Theme> themeChoice;

    @FXML
    private Text statusMessage;

    private AppState state;

    private Runnable onDatabaseReplaced;

    @FXML
    private void initialize() {
        state = AppState.getInstance();

        resolutionChoice.getItems().setAll(Resolution.values());
        themeChoice.getItems().setAll(Theme.values());

        refresh();
    }

    public void setOnDatabaseReplaced(Runnable onDatabaseReplaced) {
        this.onDatabaseReplaced = onDatabaseReplaced;
    }

    public void refresh() {
        resolutionChoice.setValue(state.getResolution());
        themeChoice.setValue(state.getTheme());
        statusMessage.setText("");
    }

    // Display button functions

    @FXML
    private void handleResolution() {
        Resolution resolution = resolutionChoice.getValue();
        if (resolution == state.getResolution()) {
            return;
        }

        try {
            state.setResolution(resolution);
            DisplaySettings.applyResolution(getStage(), resolution);
        } catch (SQLException e) {
            statusMessage.setText("The resolution could not be saved: " + e.getMessage());
        }
    }

    @FXML
    private void handleTheme() {
        // Swapping stylesheets under an open popup breaks its CSS lookups, so wait for onHidden
        if (themeChoice.isShowing()) {
            return;
        }

        Theme theme = themeChoice.getValue();
        if (theme == state.getTheme()) {
            return;
        }

        try {
            state.setTheme(theme);
            DisplaySettings.applyTheme(getStage().getScene(), theme);
        } catch (SQLException e) {
            statusMessage.setText("The theme could not be saved: " + e.getMessage());
        }
    }

    // Database button functions

    @FXML
    private void handleExport() {
        System.out.println("Export Database clicked");
        FileChooser chooser = createFileChooser("Export Database");
        chooser.setInitialFileName(EXPORT_FILE_NAME);
        File file = chooser.showSaveDialog(getStage());
        if (file == null) {
            return;
        }

        try {
            state.exportDatabase(file.toPath());
            statusMessage.setText("Database exported to " + file);
        } catch (SQLException | IOException e) {
            statusMessage.setText("The database could not be exported: " + e.getMessage());
        }
    }

    @FXML
    private void handleImport() {
        System.out.println("Import Database clicked");
        File file = createFileChooser("Import Database").showOpenDialog(getStage());
        if (file == null) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Importing replaces everything in the current database, including all worlds and settings.");
        confirmation.setHeaderText("Import " + file.getName() + "?");
        if (showDialog(confirmation).orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            state.importDatabase(file.toPath());
            finishReplacingDatabase();
        } catch (SQLException e) {
            statusMessage.setText("Nothing was imported, " + file.getName() + " is not a valid database export.");
        }
    }

    @FXML
    private void handleWipe() {
        System.out.println("Wipe Database clicked");
        Alert warning = new Alert(Alert.AlertType.WARNING,
                "This permanently deletes all worlds and settings. Export the database first to keep a copy.",
                ButtonType.OK, ButtonType.CANCEL);
        warning.setHeaderText("Wipe the current database?");
        if (showDialog(warning).orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        TextInputDialog confirmation = new TextInputDialog();
        confirmation.setHeaderText("This cannot be undone.");
        confirmation.setContentText("Type " + WIPE_CONFIRMATION_WORD + " to confirm:");
        if (!showDialog(confirmation).orElse("").equals(WIPE_CONFIRMATION_WORD)) {
            statusMessage.setText("Wipe cancelled, nothing was deleted.");
            return;
        }

        try {
            state.wipeDatabase();
            finishReplacingDatabase();
        } catch (SQLException e) {
            statusMessage.setText("The database could not be wiped: " + e.getMessage());
        }
    }

    // Utility functions

    private void finishReplacingDatabase() {
        DisplaySettings.apply(getStage());
        refresh();
        onDatabaseReplaced.run();
    }

    private FileChooser createFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQLite database (*.db)", "*.db"));
        return chooser;
    }

    private <T> Optional<T> showDialog(Dialog<T> dialog) {
        dialog.initOwner(getStage());
        dialog.getDialogPane().getStylesheets().setAll(getStage().getScene().getStylesheets());
        return dialog.showAndWait();
    }

    private Stage getStage() {
        return (Stage) settingsMenu.getScene().getWindow();
    }
}

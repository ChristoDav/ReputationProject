package reputationproject;

import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import reputationproject.controller.DisplaySettings;
import reputationproject.respository.AppState;
import reputationproject.respository.Database;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Read the save state first so the views can be built from it
        try {
            AppState.getInstance().load(Database.DEFAULT_PATH);
        } catch (SQLException | IOException e) {
            new Alert(Alert.AlertType.ERROR, "The save state could not be read: " + e.getMessage()).showAndWait();
            Platform.exit();
            return;
        }

        // Show StartMenu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home_menu.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Reputation Project");
        stage.setResizable(false);
        DisplaySettings.apply(stage);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        AppState.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

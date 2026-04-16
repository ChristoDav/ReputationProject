package reputationproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialise AppState etc
        // ...
        // Show StartMenu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/home_menu.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root));
        stage.setTitle("Reputation Project");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

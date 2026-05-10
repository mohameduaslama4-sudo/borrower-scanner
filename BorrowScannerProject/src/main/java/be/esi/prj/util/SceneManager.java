package be.esi.prj.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

/**
 * Utility class responsible for managing scene navigation in the JavaFX application.
 * <p>
 * Provides a simple method to switch the root view of the current stage.
 */
public class SceneManager {

    private SceneManager() {} // constructeur privé

    /**
     * Switches the current scene root to a new FXML view.
     *
     * @param stage the current JavaFX stage
     * @param fxmlFile the FXML file name located in the /view directory
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public static void switchScene(Stage stage, String fxmlFile) {
        try {
            Parent newRoot = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/view/" + fxmlFile)));
            stage.getScene().setRoot(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Error loading " + fxmlFile, e);
        }
    }
}
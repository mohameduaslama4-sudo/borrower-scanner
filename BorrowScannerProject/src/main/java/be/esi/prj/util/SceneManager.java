package be.esi.prj.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private SceneManager() {} // constructeur privé

    /**
     * Change le contenu de la scène actuelle sans recréer la Scene
     * @param stage La fenêtre actuelle
     * @param fxmlFile Nom du fichier FXML (ex: "InventoryView.fxml")
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
package be.esi.prj.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class SceneManager {
    public static void switchScene(Stage stage, String fxmlPath) {
        try {
            String path = normalizePath(fxmlPath);
            URL resource = SceneManager.class.getResource(path);

            if (resource == null) {
                throw new IllegalArgumentException("Fichier FXML introuvable : " + path);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root));
            } else {
                stage.getScene().setRoot(root);
            }
            stage.show();
        } catch (Exception e) {
            System.err.println("ERREUR fatale lors du switch de scène vers : " + fxmlPath);
            e.printStackTrace();
        }
    }

    private static String normalizePath(String fxmlPath) {
        if (fxmlPath == null || fxmlPath.isBlank()) {
            throw new IllegalArgumentException("Le chemin FXML est vide.");
        }

        String trimmed = fxmlPath.trim();
        String withoutLeadingSlash = trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
        String withViewPrefix = withoutLeadingSlash.contains("/") ? withoutLeadingSlash : "view/" + withoutLeadingSlash;
        return "/" + withViewPrefix;
    }
}

module be.esi.prj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Pour SQLite

    // Autorise JavaFX à charger les fichiers FXML
    opens be.esi.prj.controller to javafx.fxml;

    // CRUCIAL : Autorise JavaFX à lire les attributs de tes modèles pour les TableView
    opens be.esi.prj.model to javafx.base;

    // Si tu as des données dans Repository à afficher plus tard
    opens be.esi.prj.repository to javafx.base;

    exports be.esi.prj;
}
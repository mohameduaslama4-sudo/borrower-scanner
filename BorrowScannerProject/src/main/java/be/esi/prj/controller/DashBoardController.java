package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.service.ItemService;
import be.esi.prj.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DashBoardController {
    @FXML private Button inventoryButton, usersButton, historyButton;

    // Éléments du mini-formulaire
    @FXML private TextField quickItemName;
    @FXML private ComboBox<ItemCategory> quickItemCategory;
    @FXML private Button quickAddButton;

    private final ItemService itemService = new ItemService();

    @FXML
    public void initialize() {
        setupNavigation();

        // Remplir la ComboBox avec les catégories de l'Enum
        quickItemCategory.getItems().setAll(ItemCategory.values());

        // Action du bouton "Ajouter"
        quickAddButton.setOnAction(e -> handleQuickAdd());
    }

    private void setupNavigation() {
        inventoryButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) inventoryButton.getScene().getWindow(), "/view/InventoryView.fxml"));
        usersButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) usersButton.getScene().getWindow(), "/view/UsersView.fxml"));
        historyButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) historyButton.getScene().getWindow(), "/view/HistoryView.fxml"));
    }

    private void handleQuickAdd() {
        String name = quickItemName.getText();
        ItemCategory cat = quickItemCategory.getValue();

        if (name == null || name.isEmpty() || cat == null) {
            showAlert("Erreur", "Veuillez remplir le nom et choisir une catégorie.");
            return;
        }

        // Création de l'objet (l'ID est généré par le constructeur à 4 paramètres)
        Item newItem = new Item(name, cat, ItemStatus.AVAILABLE, "default_qr.png");

        itemService.createItem(newItem);

        // Nettoyage des champs
        quickItemName.clear();
        quickItemCategory.setValue(null);

        showAlert("Succès", "Matériel " + newItem.getId() + " ajouté avec succès !");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.repository.ItemRepository;
import be.esi.prj.repository.ItemRepositoryImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ItemFormController {
    @FXML private TextField nameField;
    @FXML private ComboBox<ItemCategory> categoryCombo;
    @FXML private ComboBox<ItemStatus> statusCombo;

    private final ItemRepository itemRepository = new ItemRepositoryImpl();

    @FXML
    public void initialize() {
        categoryCombo.getItems().setAll(ItemCategory.values());
        statusCombo.getItems().setAll(ItemStatus.values());
        statusCombo.setValue(ItemStatus.AVAILABLE); // Valeur par défaut
    }

    @FXML
    private void handleSave() {
        Item newItem = new Item(
                nameField.getText(),
                categoryCombo.getValue(),
                statusCombo.getValue(),
                "images/qr/" + nameField.getText() + ".png"
        );

        itemRepository.insert(newItem);
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
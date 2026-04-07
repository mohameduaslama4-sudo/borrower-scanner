package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.repository.ItemRepository;
import be.esi.prj.repository.ItemRepositoryImpl;
import be.esi.prj.util.SceneManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryController {

    @FXML private Button dashboardButton, usersButton, historyButton;
    @FXML private Button addButton, editButton, deleteButton;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> colQrId, colName;
    @FXML private TableColumn<Item, ItemCategory> colCategory;
    @FXML private TableColumn<Item, ItemStatus> colStatus;
    @FXML private TableColumn<Item, Item> colQrDownload;

    // --- Éléments du formulaire interne (Hidden) ---
    @FXML private VBox addItemForm;
    @FXML private TextField nameField;
    @FXML private ComboBox<ItemCategory> categoryCombo;
    @FXML private Button confirmAddButton, cancelAddButton;

    // --- Éléments de filtre (Optionnels) ---
    @FXML private TextField searchItemField;
    @FXML private ComboBox<String> filterCategory;
    @FXML private ComboBox<String> filterStatus;

    private final ItemRepository itemRepository = new ItemRepositoryImpl();
    private final ObservableList<Item> allItems = FXCollections.observableArrayList();
    private static final String ALL_CATEGORIES = "Toutes catégories";
    private static final String ALL_STATUSES = "Tous statuts";

    @FXML
    public void initialize() {
        setupNavigation();
        setupTable();
        setupForm();
        setupCrudActions();
        setupFilters();
        refreshTable();
    }

    private void setupTable() {
        colQrId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        setupQrDownloadColumn();
    }

    private void setupForm() {
        categoryCombo.getItems().setAll(ItemCategory.values());
        toggleForm(false);
        addButton.setOnAction(e -> toggleForm(true));
        cancelAddButton.setOnAction(e -> toggleForm(false));
        confirmAddButton.setOnAction(e -> handleInsert());
    }

    private void setupCrudActions() {
        editButton.disableProperty().bind(itemTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(itemTable.getSelectionModel().selectedItemProperty().isNull());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());
    }

    private void setupFilters() {
        List<String> categoryFilters = new ArrayList<>();
        categoryFilters.add(ALL_CATEGORIES);
        for (ItemCategory category : ItemCategory.values()) {
            categoryFilters.add(category.name());
        }
        filterCategory.setItems(FXCollections.observableArrayList(categoryFilters));
        filterCategory.setValue(ALL_CATEGORIES);

        List<String> statusFilters = new ArrayList<>();
        statusFilters.add(ALL_STATUSES);
        for (ItemStatus status : ItemStatus.values()) {
            statusFilters.add(status.name());
        }
        filterStatus.setItems(FXCollections.observableArrayList(statusFilters));
        filterStatus.setValue(ALL_STATUSES);

        filterCategory.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        filterStatus.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        searchItemField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void setupQrDownloadColumn() {
        colQrDownload.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        colQrDownload.setCellFactory(column -> new TableCell<>() {
            private final Button downloadButton = new Button("Télécharger QR");

            {
                downloadButton.setOnAction(event -> {
                    Item item = getTableRow().getItem();
                    if (item != null) {
                        handleDownloadQr(item);
                    }
                });
            }

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || item == null ? null : downloadButton);
            }
        });
    }

    private void toggleForm(boolean show) {
        addItemForm.setVisible(show);
        addItemForm.setManaged(show);
        if (!show) {
            nameField.clear();
            categoryCombo.setValue(null);
        }
    }

    private void handleInsert() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        ItemCategory cat = categoryCombo.getValue();

        if (name.isEmpty() || cat == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            alert.show();
            return;
        }

        Item newItem = new Item(name, cat, ItemStatus.AVAILABLE, "external-data/qr/" + sanitizeFilename(name) + ".png");
        itemRepository.insert(newItem);

        toggleForm(false);
        refreshTable();
    }

    private void handleEdit() {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier le matériel");
        dialog.setHeaderText("Modifier l'élément : " + selectedItem.getId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameInput = new TextField(selectedItem.getName());
        ComboBox<ItemCategory> categoryInput = new ComboBox<>(FXCollections.observableArrayList(ItemCategory.values()));
        categoryInput.setValue(selectedItem.getCategory());

        ComboBox<ItemStatus> statusInput = new ComboBox<>(FXCollections.observableArrayList(ItemStatus.values()));
        statusInput.setValue(selectedItem.getStatus());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Nom"), 0, 0);
        form.add(nameInput, 1, 0);
        form.add(new Label("Catégorie"), 0, 1);
        form.add(categoryInput, 1, 1);
        form.add(new Label("Statut"), 0, 2);
        form.add(statusInput, 1, 2);

        dialog.getDialogPane().setContent(form);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        String updatedName = nameInput.getText() == null ? "" : nameInput.getText().trim();
        ItemCategory updatedCategory = categoryInput.getValue();
        ItemStatus updatedStatus = statusInput.getValue();

        if (updatedName.isEmpty() || updatedCategory == null || updatedStatus == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs de modification.").showAndWait();
            return;
        }

        Item updatedItem = new Item(
                selectedItem.getId(),
                updatedName,
                updatedCategory,
                updatedStatus,
                selectedItem.getQrPath()
        );

        itemRepository.update(updatedItem);
        refreshTable();
        selectItemById(updatedItem.getId());
    }

    private void handleDelete() {
        Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Supprimer l'élément \"" + selectedItem.getName() + "\" ?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirm.setHeaderText("Confirmation de suppression");

        Optional<ButtonType> response = confirm.showAndWait();
        if (response.isEmpty() || response.get() != ButtonType.YES) {
            return;
        }

        try {
            itemRepository.delete(selectedItem.getId());
            refreshTable();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Suppression impossible");
            error.setHeaderText(null);
            error.setContentText("Impossible de supprimer cet élément (lié à un emprunt, par exemple).");
            error.showAndWait();
        }
    }

    private void handleDownloadQr(Item item) {
        String qrPath = item.getQrPath();
        if (qrPath == null || qrPath.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Aucun chemin de code QR n'est associé à cet objet.").showAndWait();
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Télécharger le code QR");
        chooser.setInitialFileName(item.getId() + "-qr.png");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));

        Stage stage = (Stage) itemTable.getScene().getWindow();
        File destination = chooser.showSaveDialog(stage);
        if (destination == null) {
            return;
        }

        try {
            copyQrToDestination(qrPath, destination.toPath());
            new Alert(Alert.AlertType.INFORMATION, "Code QR téléchargé avec succès.").showAndWait();
        } catch (Exception ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Téléchargement impossible");
            error.setHeaderText(null);
            error.setContentText("Le fichier QR associé est introuvable: " + qrPath);
            error.showAndWait();
        }
    }

    private void copyQrToDestination(String qrPath, Path destination) throws Exception {
        Path path = Path.of(qrPath);

        if (!path.isAbsolute()) {
            path = Path.of(System.getProperty("user.dir")).resolve(path).normalize();
        }

        if (Files.exists(path)) {
            Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
            return;
        }

        String resourcePath = qrPath.startsWith("/") ? qrPath.substring(1) : qrPath;
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            File fallback = new File(qrPath);
            if (!fallback.isAbsolute()) {
                fallback = new File(System.getProperty("user.dir"), qrPath);
            }
            if (!fallback.exists()) {
                throw new FileNotFoundException("QR introuvable: " + qrPath);
            }
            inputStream = new FileInputStream(fallback);
        }

        try (InputStream source = inputStream) {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void applyFilters() {
        String search = searchItemField.getText() == null ? "" : searchItemField.getText().trim().toLowerCase();
        String selectedCategory = filterCategory.getValue();
        String selectedStatus = filterStatus.getValue();

        List<Item> filtered = allItems.stream()
                .filter(item -> search.isBlank()
                        || item.getName().toLowerCase().contains(search)
                        || item.getId().toLowerCase().contains(search))
                .filter(item -> selectedCategory == null
                        || ALL_CATEGORIES.equals(selectedCategory)
                        || item.getCategory().name().equals(selectedCategory))
                .filter(item -> selectedStatus == null
                        || ALL_STATUSES.equals(selectedStatus)
                        || item.getStatus().name().equals(selectedStatus))
                .toList();

        itemTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void refreshTable() {
        try {
            allItems.setAll(itemRepository.findAll());
            applyFilters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectItemById(String itemId) {
        for (Item item : itemTable.getItems()) {
            if (item.getId().equals(itemId)) {
                itemTable.getSelectionModel().select(item);
                itemTable.scrollTo(item);
                break;
            }
        }
    }

    private String sanitizeFilename(String raw) {
        return raw.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private void setupNavigation() {
        dashboardButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) dashboardButton.getScene().getWindow(), "/view/DashBoardView.fxml"));

        usersButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) usersButton.getScene().getWindow(), "/view/UsersView.fxml"));

        historyButton.setOnAction(e -> SceneManager.switchScene(
                (Stage) historyButton.getScene().getWindow(), "/view/HistoryView.fxml"));
    }
}

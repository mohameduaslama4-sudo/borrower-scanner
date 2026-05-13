package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.service.ItemService;
import be.esi.prj.service.QRService;
import be.esi.prj.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Controller responsible for managing the inventory view.
 * <p>
 * Handles item management, filtering, editing, deletion,
 * QR code download, and navigation between application views.
 */
public class InventoryController {

    // SERVICES
    private final ItemService itemService = ItemService.getInstance();
    private final QRService qrService = new QRService();

    // NAVIGATION
    @FXML private Button dashboardButton;
    @FXML private Button usersButton;
    @FXML private Button historyButton;

    // TABLE
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, String> colQrId;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, ItemCategory> colCategory;
    @FXML private TableColumn<Item, ItemStatus> colStatus;
    @FXML private TableColumn<Item, String> colQrPath;

    // FILTERS
    private FilteredList<Item> filteredList;

    @FXML private TextField searchItemField;
    @FXML private ComboBox<ItemCategory> filterCategory;
    @FXML private ComboBox<ItemStatus> filterStatus;

    // ACTIONS
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    /**
     * Initializes the inventory view and loads item data.
     */
    @FXML
    public void initialize() {

        initNavigation();
        initTable();
        initFilters();

        VirtualThreadExecutor.run(() -> {
            itemService.load();
            Platform.runLater(() -> {
                filteredList = new FilteredList<>(itemService.getItemsObservable(), p -> true);
                itemTable.setItems(filteredList);
            });

        });

        addButton.setOnAction(e -> handleInsertItem());
        deleteButton.setOnAction(e -> handleDeleteItem());
        editButton.setOnAction(e -> handleEditItem());
    }

    /**
     * Initializes navigation button actions.
     */
    private void initNavigation() {

        dashboardButton.setOnAction(e ->
                SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "DashBoardView.fxml"));

        usersButton.setOnAction(e ->
                SceneManager.switchScene((Stage) usersButton.getScene().getWindow(), "UsersView.fxml"));

        historyButton.setOnAction(e ->
                SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));
    }

    /**
     * Configures table columns and QR code download actions.
     */
    private void initTable() {

        colQrId.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCategory.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCategory()));
        colStatus.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getStatus()));
        colQrPath.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQrPath()));

        colQrPath.setCellFactory(tc -> new TableCell<>() {
            private final Label link = new Label("📥 Télécharger QR Code");
            {
                link.getStyleClass().add("qr-link");
                link.setOnMouseClicked(e -> {
                    Item i = getTableView().getItems().get(getIndex());
                    downloadQr(i);
                });

            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                setGraphic(empty ? null : link);
            }
        });
    }

    /**
     * Initializes search and filtering controls.
     */
    private void initFilters() {

        filterCategory.setItems(FXCollections.observableArrayList(
                Stream.concat(Stream.of((ItemCategory) null), Stream.of(ItemCategory.values())).toList()
        ));
        filterCategory.setOnAction(e -> applyFilters());

        filterStatus.setItems(FXCollections.observableArrayList(
                Stream.concat(Stream.of((ItemStatus) null), Stream.of(ItemStatus.values())).toList()
        ));
        filterStatus.setOnAction(e -> applyFilters());

        searchItemField.textProperty().addListener((o, a, b) -> applyFilters());
    }

    /**
     * Applies search and filter conditions to displayed items.
     */
    private void applyFilters() {

        ItemCategory category = filterCategory.getValue();
        ItemStatus status = filterStatus.getValue();
        String keyword = searchItemField.getText();

        filteredList.setPredicate(item -> {

            if (category != null && item.getCategory() != category) return false;
            if (status != null && item.getStatus() != status) return false;

            return keyword == null || keyword.isBlank()
                    || item.getName().toLowerCase().contains(keyword.toLowerCase())
                    || item.getId().toLowerCase().contains(keyword.toLowerCase());
        });
    }

    /**
     * Opens the item creation dialog and inserts a new item.
     */
    private void handleInsertItem() {

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");

        ComboBox<ItemCategory> categoryBox =
                new ComboBox<>(FXCollections.observableArrayList(ItemCategory.values()));
        categoryBox.setPromptText("Catégorie");

        HBox content = new HBox(20, categoryBox, nameField);
        content.setAlignment(Pos.CENTER);

        Dialog<Item> dialog = createDialog("Ajouter item", "Créer un item", content);
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(nameField.textProperty().isEmpty()
                .or(categoryBox.valueProperty().isNull()));

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK &&
                    !nameField.getText().isBlank() &&
                    categoryBox.getValue() != null) {

                return new Item(null, nameField.getText().toUpperCase(), categoryBox.getValue(), ItemStatus.AVAILABLE, null);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(item ->
                VirtualThreadExecutor.run(() -> {

                    try {
                        Item i = itemService.addItem(item);
                        Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION,
                                "Item " + i.getId() + " ajouté !").show());

                    } catch (Exception e) {
                        Platform.runLater(() ->
                                new Alert(Alert.AlertType.ERROR,
                                        "Une erreur s'est produite durant l'ajout").show()
                        );
                    }
                })
        );
    }

    /**
     * Deletes the currently selected item after confirmation.
     */
    private void handleDeleteItem() {

        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez-vous vraiment supprimer " + selected.getName() + " ?");

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                VirtualThreadExecutor.run(() -> itemService.deleteItem(selected.getId()));
            }
        });
    }

    /**
     * Opens the item edition dialog and updates the selected item.
     */
    private void handleEditItem() {

        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        ComboBox<ItemCategory> categoryBox =
                new ComboBox<>(FXCollections.observableArrayList(ItemCategory.values()));
        categoryBox.setValue(selected.getCategory());

        ComboBox<ItemStatus> statusBox =
                new ComboBox<>(FXCollections.observableArrayList(ItemStatus.values()));
        statusBox.setValue(selected.getStatus());

        HBox content = new HBox(20, categoryBox, statusBox);
        content.setAlignment(Pos.CENTER);

        Dialog<ButtonType> dialog =
                createDialog("Modifier item", "Edit item", content);

        dialog.showAndWait().ifPresent(res -> {

            if (res == ButtonType.OK) {

                VirtualThreadExecutor.run(() -> {
                    selected.setCategory(categoryBox.getValue());
                    selected.setStatus(statusBox.getValue());
                    itemService.updateItem(selected);
                });
            }
        });
    }

    /**
     * Downloads the QR code associated with an item.
     *
     * @param item item containing the QR code
     */
    private void downloadQr(Item item) {

        if (item == null || item.getQrPath() == null) return;
        Path dest = qrService.chooseDownloadDestination(item.getId(), itemTable.getScene().getWindow());
        if (dest == null) return;

        VirtualThreadExecutor.run(() -> {
            try {
                qrService.downloadQRCode(item.getQrPath(), dest);
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.INFORMATION, "Téléchargement réussi !").show()
                );

            } catch (IllegalStateException e) {
                Platform.runLater(() ->
                        new Alert(Alert.AlertType.ERROR,
                                "Échec du téléchargement : " + e.getMessage()).show()
                );
            }
        });
    }

    /**
     * Creates and configures a reusable dialog window.
     *
     * @param title dialog title
     * @param header dialog header text
     * @param content dialog content
     * @param <T> dialog result type
     * @return configured dialog
     */
    private <T> Dialog<T> createDialog(String title, String header, javafx.scene.layout.Region content) {

        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(400, 200);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        return dialog;
    }
}
package be.esi.prj.controller;

import be.esi.prj.model.Borrower;
import be.esi.prj.repository.BorrowerRepository;
import be.esi.prj.repository.BorrowerRepositoryImpl;
import be.esi.prj.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UsersController {

    @FXML private Button dashboardButton, inventoryButton, historyButton;
    @FXML private Button addUserButton, cancelAddButton, confirmAddButton;

    @FXML private TableView<Borrower> userTable;
    @FXML private TableColumn<Borrower, Integer> colId;
    @FXML private TableColumn<Borrower, String> colLastName;
    @FXML private TableColumn<Borrower, String> colFirstName;
    @FXML private TableColumn<Borrower, String> colEmail;
    @FXML private VBox addUserForm;
    @FXML private TextField newLastName, newFirstName, newEmail;

    private final BorrowerRepository borrowerRepository = new BorrowerRepositoryImpl();
    private final ObservableList<Borrower> users = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupNavigation();
        setupAddUserForm();

        // Liaison colonnes <-> Attributs de la classe Borrower
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        userTable.setItems(users);
        refreshTable();
    }

    private void setupNavigation() {
        dashboardButton.setOnAction(e -> SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "/view/DashBoardView.fxml"));
        inventoryButton.setOnAction(e -> SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "/view/InventoryView.fxml"));
        historyButton.setOnAction(e -> SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "/view/HistoryView.fxml"));
    }

    private void setupAddUserForm() {
        toggleAddForm(false);
        addUserButton.setOnAction(e -> toggleAddForm(true));
        cancelAddButton.setOnAction(e -> toggleAddForm(false));
        confirmAddButton.setOnAction(e -> handleConfirmAddUser());
    }

    private void handleConfirmAddUser() {
        String lastName = safeTrim(newLastName.getText());
        String firstName = safeTrim(newFirstName.getText());
        String email = safeTrim(newEmail.getText());

        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs incomplets");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir nom, prénom et email.");
            alert.showAndWait();
            return;
        }

        try {
            borrowerRepository.insert(new Borrower(0, firstName, lastName, email));
            refreshTable();
            toggleAddForm(false);

            if (!users.isEmpty()) {
                Borrower inserted = users.get(users.size() - 1);
                userTable.getSelectionModel().select(inserted);
                userTable.scrollTo(inserted);
            }
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de sauvegarde");
            alert.setHeaderText(null);
            alert.setContentText("Impossible d'enregistrer le nouvel utilisateur.");
            alert.showAndWait();
        }
    }

    private void toggleAddForm(boolean visible) {
        addUserForm.setVisible(visible);
        addUserForm.setManaged(visible);

        if (!visible) {
            newLastName.clear();
            newFirstName.clear();
            newEmail.clear();
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private void refreshTable() {
        users.setAll(borrowerRepository.findAll());
    }
}

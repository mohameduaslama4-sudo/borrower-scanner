package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.service.*;
import be.esi.prj.util.SceneManager;
import be.esi.prj.util.VirtualThreadExecutor;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UsersController {

    private final BorrowerService borrowerService = BorrowerService.getInstance();
    private final LoanService loanService = LoanService.getInstance();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Button dashboardButton;
    @FXML private Button inventoryButton;
    @FXML private Button historyButton;

    @FXML private TableView<Borrower> userTable;
    @FXML private TableColumn<Borrower, Integer> colId;
    @FXML private TableColumn<Borrower, String> colLastName;
    @FXML private TableColumn<Borrower, String> colFirstName;
    @FXML private TableColumn<Borrower, String> colEmail;

    @FXML private TextField searchUserField;

    @FXML private VBox rightPane;
    @FXML private Label detailLastNameLabel;
    @FXML private Label detailFirstNameLabel;
    @FXML private Label detailEmailLabel;

    @FXML private Label currentLoansCountLabel;
    @FXML private ListView<String> currentLoansList;
    @FXML private ListView<String> recentHistoryList;

    @FXML private Button updateEmailButton;
    @FXML private Button deleteUserButton;

    @FXML private VBox addUserForm;
    @FXML private TextField newLastName;
    @FXML private TextField newFirstName;
    @FXML private TextField newEmail;

    @FXML private Button addUserButton;
    @FXML private Button cancelAddButton;
    @FXML private Button confirmAddButton;

    private FilteredList<Borrower> filteredBorrowers;

    @FXML
    public void initialize() {

        initNavigation();
        initTable();
        initActions();

        borrowerService.load();
        loanService.load();
        filteredBorrowers = new FilteredList<>(borrowerService.getBorrowersObservable(), b -> true);
        userTable.setItems(filteredBorrowers);
    }

    private void initNavigation() {

        dashboardButton.setOnAction(e ->
                SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(), "DashBoardView.fxml"));

        inventoryButton.setOnAction(e ->
                SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "InventoryView.fxml"));

        historyButton.setOnAction(e ->
                SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));
    }

    private void initTable() {

        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));
        colLastName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        colFirstName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));

        userTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> showDetails(selected));
    }

    private void initActions() {

        searchUserField.textProperty().addListener((o, a, b) -> applyFilter(b));

        addUserButton.setOnAction(e -> toggleAddForm(true));
        cancelAddButton.setOnAction(e -> toggleAddForm(false));
        confirmAddButton.setOnAction(e -> addUser());

        updateEmailButton.setOnAction(e -> updateEmail());
        deleteUserButton.setOnAction(e -> deleteUser());
    }

    private void applyFilter(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            filteredBorrowers.setPredicate(b -> true);
            return;
        }

        String lower = keyword.toLowerCase();
        filteredBorrowers.setPredicate(b -> b.getFirstName().toLowerCase().contains(lower)
                || b.getLastName().toLowerCase().contains(lower) || b.getEmail().toLowerCase().contains(lower));
    }

    private void showDetails(Borrower b) {

        if (b == null) {
            rightPane.setVisible(false);
            rightPane.setManaged(false);
            return;
        }

        rightPane.setVisible(true);
        rightPane.setManaged(true);

        detailLastNameLabel.setText(b.getLastName());
        detailFirstNameLabel.setText(b.getFirstName());
        detailEmailLabel.setText(b.getEmail());

        loadUserLoans(b);
    }

    private void loadUserLoans(Borrower b) {

        VirtualThreadExecutor.run(() -> {

            List<Loan> allLoans = loanService.getLoansByBorrower(b.getId());
            List<Loan> currentLoans = allLoans.stream().filter(l -> l.getReturnDate() == null).toList();
            List<String> current = currentLoans.stream().map(this::formatCurrentLoan).toList();
            List<String> history = allLoans.stream().sorted((a, c) ->
                            c.getBorrowedDate().compareTo(a.getBorrowedDate())).limit(10).map(this::formatHistoryLoan).toList();

                Platform.runLater(() -> {
                    currentLoansCountLabel.setText("Emprunts en cours : " + currentLoans.size());
                    currentLoansList.setItems(FXCollections.observableArrayList(current));
                    recentHistoryList.setItems(FXCollections.observableArrayList(history));
                });
        });
    }

    private String formatCurrentLoan(Loan l) {
        return l.getItemId() + " | À rendre avant le " + l.getDueDate().toLocalDate().format(FORMATTER);
    }

    private String formatHistoryLoan(Loan l) {

        String status = (l.getReturnDate() == null) ? "En cours"
                : "Remis le " + l.getReturnDate().toLocalDate().format(FORMATTER);

        return l.getBorrowedDate().toLocalDate().format(FORMATTER) + " | " + l.getItemId() + " | " + status;
    }

    private void addUser() {

        String last = newLastName.getText().toUpperCase();
        String first = newFirstName.getText().toUpperCase();
        String email = newEmail.getText().toUpperCase();

        VirtualThreadExecutor.run(() -> {

            try {
                Borrower b = borrowerService.addBorrower(last, first, email);

                Platform.runLater(() -> {
                    borrowerService.getBorrowersObservable().add(b);
                    clearForm();
                    toggleAddForm(false);
                    alert(Alert.AlertType.INFORMATION, "Utilisateur ajouté");
                });

            } catch (IllegalArgumentException e) {
                Platform.runLater(() ->
                        alert(Alert.AlertType.ERROR, e.getMessage())
                );

            } catch (Exception e) {
                Platform.runLater(() -> alert(Alert.AlertType.ERROR,
                        "Une erreur s'est produite lors de l'ajout"));
            }
        });
    }

    private void deleteUser() {

        Borrower selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                selected.getFirstName() + " " + selected.getLastName());
        confirm.setHeaderText("Voulez-vous vraiment supprimer cet utilisateur ?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                VirtualThreadExecutor.run(() -> {
                    borrowerService.deleteBorrower(selected.getId());
                    Platform.runLater(() -> {
                        borrowerService.getBorrowersObservable().remove(selected);
                        showDetails(null);
                    });
                });
            }
        });
    }

    private void updateEmail() {

        Borrower selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getEmail());
        dialog.setHeaderText("Modifier email");
        dialog.showAndWait().ifPresent(mail -> {

            VirtualThreadExecutor.run(() -> {
                try {
                    borrowerService.updateBorrowerEmail(selected.getId(), mail);
                    Platform.runLater(() -> {
                        userTable.refresh();
                        showDetails(selected);
                    });

                } catch (IllegalArgumentException e) {
                    Platform.runLater(() -> alert(Alert.AlertType.ERROR, e.getMessage()));

                } catch (Exception e) {
                    Platform.runLater(() -> alert(Alert.AlertType.ERROR,
                            "Une erreur s'est produite durant la màj"));
                }
            });
        });
    }

    private void toggleAddForm(boolean show) {
        addUserForm.setVisible(show);
        addUserForm.setManaged(show);
    }

    private void clearForm() {
        newLastName.clear();
        newFirstName.clear();
        newEmail.clear();
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}
package be.esi.prj.controller;

import be.esi.prj.model.*;
import be.esi.prj.service.*;
import be.esi.prj.util.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller responsible for managing the dashboard view.
 * <p>
 * Handles navigation, statistics display, quick item creation,
 * borrowing/return workflows, and QR scan interactions.
 */
public class DashBoardController {

    // SERVICES
    private final BorrowerService borrowerService = BorrowerService.getInstance();
    private final LoanService loanService = LoanService.getInstance();
    private final ItemService itemService = ItemService.getInstance();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // NAVIGATION
    @FXML private Button inventoryButton;
    @FXML private Button usersButton;
    @FXML private Button historyButton;
    @FXML private Button scanButton;

    // STATS
    @FXML private VBox activeLoansBox;
    @FXML private VBox overdueLoansBox;
    @FXML private VBox todayBorrowBox;
    @FXML private VBox todayReturnBox;

    // LABELS
    @FXML private Label borrowedLabel;
    @FXML private Label lateLabel;
    @FXML private Label todayBorrowLabel;
    @FXML private Label todayReturnLabel;

    // QUICK FORM
    @FXML private TextField quickItemName;
    @FXML private ComboBox<ItemCategory> quickItemCategory;
    @FXML private Button quickAddButton;

    /**
     * Initializes the dashboard view and loads application data.
     * <p>
     * Sets navigation actions, initializes statistics interactions,
     * and loads borrowers, items, and loans asynchronously.
     */
    @FXML
    public void initialize() {

        inventoryButton.setOnAction(e ->
                SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(), "InventoryView.fxml"));

        usersButton.setOnAction(e ->
                SceneManager.switchScene((Stage) usersButton.getScene().getWindow(), "UsersView.fxml"));

        historyButton.setOnAction(e ->
                SceneManager.switchScene((Stage) historyButton.getScene().getWindow(), "HistoryView.fxml"));

        scanButton.setOnAction(e -> openScanDialog());

        quickItemCategory.setItems(FXCollections.observableArrayList(ItemCategory.values()));
        quickAddButton.setOnAction(e -> quickAddItem());

        initStatsClicks();

        // LOAD DATA
        VirtualThreadExecutor.run(() -> {
            borrowerService.load();
            loanService.load();
            itemService.load();
            Platform.runLater(this::updateStats);
        });
    }

    /**
     * Displays a simple alert dialog.
     *
     * @param type alert type
     * @param msg message to display
     */
    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }

    /**
     * Opens the QR scan dialog used to identify an item.
     */
    private void openScanDialog() {

        VirtualThreadExecutor.run(borrowerService::load);

        Dialog<String> dialog = createDialog("Scanner", "Scanner un item");
        TextField field = new TextField();
        field.setPromptText("scanner ici...");

        VBox root = new VBox(20, field);
        root.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE));

        Platform.runLater(field::requestFocus);

        field.setOnAction(e -> {
            String value = field.getText().replace("§", "-");
            if (value == null || value.isBlank()) return;

            VirtualThreadExecutor.run(() -> {
                Item item = itemService.getItemById(value);

                Platform.runLater(() -> {
                    if (item == null) {
                        alert(Alert.AlertType.ERROR, "Item non reconnu ❌");
                    } else {
                        dialog.close();
                        openActionDialog(item);
                    }
                });

            });
        });
        dialog.showAndWait();
    }

    /**
     * Opens the action dialog associated with a scanned item.
     *
     * @param item scanned item
     */
    private void openActionDialog(Item item) {

        Dialog<Void> dialog = createDialog("Actions", "Item : " + item.getId());
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE));

        ComboBox<Borrower> cb = createBorrowerComboBox();

        Button emprunt = new Button("Emprunt");
        Button retour = new Button("Retour");
        Button info = new Button("Info");
        emprunt.disableProperty().bind(cb.valueProperty().isNull());

        HBox buttons = new HBox(10, emprunt, retour, info);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, cb, buttons);
        root.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(root);

        emprunt.setOnAction(e -> handleBorrow(item, cb.getValue(), dialog));
        retour.setOnAction(e -> handleReturn(item, dialog));
        info.setOnAction(e -> showItemInfo(item));

        dialog.show();
    }

    /**
     * Displays detailed information about an item.
     *
     * @param item item to display
     */
    private void showItemInfo(Item item) {

        String message = "ID: " + item.getId() + "\n" + "Nom: " + item.getName() + "\n" +
                        "Catégorie: " + item.getCategory() + "\n" + "Statut: " + item.getStatus();
        alert(Alert.AlertType.INFORMATION, message);
    }

    /**
     * Creates a new loan for the selected borrower and item.
     *
     * @param item borrowed item
     * @param borrower selected borrower
     * @param dialog dialog to close after success
     */
    private void handleBorrow(Item item, Borrower borrower, Dialog<?> dialog) {

        VirtualThreadExecutor.run(() -> {
            try {
                Loan l = new Loan(item.getId(), borrower.getId());
                loanService.createLoan(l);

                Platform.runLater(() -> {
                    updateStats();
                    dialog.close();

                    alert(Alert.AlertType.INFORMATION,
                            "Emprunt enregistré !\nItem: " + item.getId() +
                                    "\nUser: " + borrower.getFirstName() + " " + borrower.getLastName() +
                                    "\nA remettre avant le : " + l.getDueDate()
                                    .toLocalDate()
                                    .format(DATE_FORMATTER)
                    );
                });

            } catch (IllegalStateException e) {
                Platform.runLater(() ->
                        alert(Alert.AlertType.ERROR, e.getMessage()));
            }
        });
    }

    /**
     * Processes the return of a borrowed item.
     *
     * @param item returned item
     * @param dialog dialog to close after success
     */
    private void handleReturn(Item item, Dialog<?> dialog) {

        VirtualThreadExecutor.run(() -> {
            try {
                Loan loan = loanService.getActiveLoans().stream()
                        .filter(l -> l.getItemId().equals(item.getId())).findFirst()
                        .orElseThrow(() -> new IllegalStateException("Aucun prêt actif trouvé pour " + item.getId()));

                Loan updatedLoan = loanService.returnLoan(loan.getId());
                Borrower borrower = borrowerService.getBorrowerById(loan.getBorrowerId());

                Platform.runLater(() -> {
                    updateStats();
                    dialog.close();

                    alert(Alert.AlertType.INFORMATION,
                            "Retour OK\nItem: " + item.getId() +
                                    "\nUser: " + borrower.getFirstName() + " " + borrower.getLastName() +
                                    "\nRemis le : " + updatedLoan.getReturnDate()
                                    .toLocalDate().format(DATE_FORMATTER)
                    );
                });

            } catch (IllegalStateException e) {
                Platform.runLater(() ->
                        alert(Alert.AlertType.ERROR, e.getMessage()));
            }
        });
    }

    /**
     * Creates and configures the borrower selection combo box.
     *
     * @return configured borrower combo box
     */
    private ComboBox<Borrower> createBorrowerComboBox() {

        ComboBox<Borrower> cb = new ComboBox<>();
        cb.setPrefWidth(250);
        cb.setPromptText("Choisissez un utilisateur");
        cb.setItems(borrowerService.getBorrowersObservable());

        cb.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Borrower b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : format(b));
            }
        });

        cb.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Borrower b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty || b == null ? null : format(b));
            }
        });
        return cb;
    }

    /**
     * Updates dashboard statistics asynchronously.
     */
    private void updateStats() {

        VirtualThreadExecutor.run(() -> {
             int active = loanService.countActiveLoans();
             int late = loanService.countOverdueLoans();
             int todayBorrow = loanService.countLoansToday();
             int todayReturn = loanService.countReturnsToday();

             Platform.runLater(() -> {
                 borrowedLabel.setText(String.valueOf(active));
                 lateLabel.setText(String.valueOf(late));
                 todayBorrowLabel.setText(String.valueOf(todayBorrow));
                 todayReturnLabel.setText(String.valueOf(todayReturn));
             });

        });
    }

    /**
     * Initializes click events for statistics cards.
     */
    private void initStatsClicks() {

        activeLoansBox.setOnMouseClicked(e ->
                VirtualThreadExecutor.run(() -> {
                    List<Loan> data = loanService.getActiveLoans();
                    Platform.runLater(() -> showStatDialog("Emprunts actifs", data));
                }));

        overdueLoansBox.setOnMouseClicked(e ->
                VirtualThreadExecutor.run(() -> {
                    List<Loan> data = loanService.getOverdueLoans();
                    Platform.runLater(() -> showStatDialog("Emprunts en retards", data));
                }));

        todayBorrowBox.setOnMouseClicked(e ->
                VirtualThreadExecutor.run(() -> {
                    List<Loan> data = loanService.getLoansByDateRange(LocalDate.now(), LocalDate.now());
                    Platform.runLater(() -> showStatDialog("Emprunts du jour", data));
                }));

        todayReturnBox.setOnMouseClicked(e ->
                VirtualThreadExecutor.run(() -> {
                    List<Loan> data = loanService.getLoansObservable().stream()
                            .filter(l -> l.getReturnDate() != null &&
                                    l.getReturnDate().toLocalDate().equals(LocalDate.now())).toList();
                        Platform.runLater(() -> showStatDialog("Remis du jour", data));
                }));
    }

    /**
     * Displays a dialog containing a table of loans.
     *
     * @param title dialog title
     * @param loans loans to display
     */
    private void showStatDialog(String title, List<Loan> loans) {

        TableView<Loan> table = new TableView<>();

        TableColumn<Loan, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getItemId()));

        TableColumn<Loan, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getBorrowerId())));

        TableColumn<Loan, String> dueCol = new TableColumn<>("Échéance");
        dueCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDueDate().toLocalDate().format(DATE_FORMATTER)));

        table.getColumns().addAll(List.of(itemCol, userCol, dueCol));
        table.setItems(FXCollections.observableArrayList(loans));

        Dialog<Void> dialog = createDialog(title, "Liste des prêts");
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.show();
    }

    /**
     * Creates and configures a reusable dialog window.
     *
     * @param title dialog title
     * @param header dialog header text
     * @param <T> dialog result type
     * @return configured dialog
     */
    private <T> Dialog<T> createDialog(String title, String header) {

        Dialog<T> d = new Dialog<>();
        d.setTitle(title);
        d.setHeaderText(header);
        d.getDialogPane().setPrefSize(400, 200);

        return d;
    }

    /**
     * Quickly creates a new item from dashboard inputs.
     */
    private void quickAddItem() {

        String name = quickItemName.getText().trim().toUpperCase();
        ItemCategory cat = quickItemCategory.getValue();

        if (name.isBlank() || cat == null) {
            alert(Alert.AlertType.ERROR, "Nom et catégorie requis");
            return;
        }

        VirtualThreadExecutor.run(() -> {

            try {
                itemService.addItem(new Item(null, name, cat, ItemStatus.AVAILABLE, null));
                Platform.runLater(() -> {
                    quickItemName.clear();
                    quickItemCategory.setValue(null);
                    updateStats();
                    alert(Alert.AlertType.INFORMATION, "Item " + name + " ajouté avec succès");});

            } catch (Exception e) {
                Platform.runLater(() -> alert(Alert.AlertType.ERROR,
                        "Une erreur s'est produite durant l'ajout"));
            }
        });
    }

    /**
     * Formats borrower information for display.
     *
     * @param b borrower to format
     * @return formatted borrower string
     */
    private String format(Borrower b) {
        return b.getId() + " - " + b.getFirstName() + " " + b.getLastName();
    }
}
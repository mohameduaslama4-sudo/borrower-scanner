package be.esi.prj.controller;

import be.esi.prj.model.Loan;
import be.esi.prj.service.LoanService;
import be.esi.prj.util.SceneManager;
import be.esi.prj.util.VirtualThreadExecutor;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * Controller responsible for managing the loan history view.
 * <p>
 * Handles loan display, filtering, searching, sorting,
 * and navigation between application views.
 */
public class HistoryController {

    // SERVICES
    private static final LoanService loanService = LoanService.getInstance();

    // CONSTANTS
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy  |  HH:mm");

    private static final String STATUS_EN_COURS = "PRÊT EN COURS";
    private static final String STATUS_REMIS = "REMIS";

    // NAVIGATION
    @FXML private Button dashboardButton;
    @FXML private Button inventoryButton;
    @FXML private Button usersButton;

    // TABLE
    @FXML private TableView<Loan> logTable;

    @FXML private TableColumn<Loan, String> colLoanId;
    @FXML private TableColumn<Loan, String> colItem;
    @FXML private TableColumn<Loan, String> colBorrower;
    @FXML private TableColumn<Loan, String> colBorrowDate;
    @FXML private TableColumn<Loan, String> colDueDate;
    @FXML private TableColumn<Loan, String> colReturnDate;
    @FXML private TableColumn<Loan, String> colStatus;

    // FILTERS
    @FXML private TextField searchLogField;
    @FXML private ComboBox<String> filterAction;
    @FXML private ComboBox<String> sortDate;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    // DATA
    private List<Loan> baseData;

    /**
     * Initializes the history view and loads loan data.
     */
    @FXML
    public void initialize() {
        initNavigation();
        initTable();
        initFilters();
        loadData();
    }

    /**
     * Initializes navigation button actions.
     */
    private void initNavigation() {

        dashboardButton.setOnAction(e ->
                SceneManager.switchScene((Stage) dashboardButton.getScene().getWindow(),
                        "DashBoardView.fxml"));

        inventoryButton.setOnAction(e ->
                SceneManager.switchScene((Stage) inventoryButton.getScene().getWindow(),
                        "InventoryView.fxml"));

        usersButton.setOnAction(e ->
                SceneManager.switchScene((Stage) usersButton.getScene().getWindow(),
                        "UsersView.fxml"));
    }

    /**
     * Configures table columns and displayed values.
     */
    private void initTable() {

        colLoanId.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getId()))
        );

        colItem.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getItemId())
        );

        colBorrower.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getBorrowerId()))
        );

        colBorrowDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getBorrowedDate().format(FORMATTER))
        );

        colDueDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDueDate().format(FORMATTER))
        );

        colReturnDate.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getReturnDate() == null ? "-" :
                        d.getValue().getReturnDate().format(FORMATTER))
        );

        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getReturnDate() == null ? STATUS_EN_COURS : STATUS_REMIS)
        );
    }

    /**
     * Initializes filtering and sorting controls.
     */
    private void initFilters() {

        filterAction.setItems(FXCollections.observableArrayList(
                "TOUS", STATUS_EN_COURS, STATUS_REMIS
        ));
        filterAction.setValue("TOUS");

        sortDate.setItems(FXCollections.observableArrayList(
                "DÉCROISSANT", "CROISSANT"
        ));
        sortDate.setValue("DÉCROISSANT");

        filterAction.setOnAction(e -> applyFilters());
        sortDate.setOnAction(e -> applyFilters());

        searchLogField.textProperty().addListener((o, a, b) -> applyFilters());
        startDatePicker.valueProperty().addListener((o, a, b) -> applyFilters());
        endDatePicker.valueProperty().addListener((o, a, b) -> applyFilters());
    }

    /**
     * Loads loan data asynchronously and updates the table.
     */
    private void loadData() {

        VirtualThreadExecutor.run(() -> {

            try {
                loanService.load();
                baseData = loanService.getLoansObservable();

                Platform.runLater(() -> {
                    logTable.setItems(FXCollections.observableArrayList(baseData));
                    applyFilters();
                });

            } catch (Exception e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR,
                        "Erreur chargement : " + e.getMessage()).show());
            }
        });
    }

    /**
     * Applies search, filter, and sorting operations to the table data.
     */
    private void applyFilters() {

        if (baseData == null) return;

        String keyword = searchLogField.getText();
        String action = filterAction.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        boolean ascending = "CROISSANT".equals(sortDate.getValue());

        List<Loan> result = baseData.stream()

                .filter(l -> matchAction(l, action))
                .filter(l -> matchDate(l, start, end))
                .filter(l -> matchSearch(l, keyword))

                .sorted(Comparator.comparing(Loan::getBorrowedDate)).toList();

        if (!ascending) {
            result = result.stream()
                    .sorted(Comparator.comparing(Loan::getBorrowedDate).reversed()).toList();
        }

        logTable.setItems(FXCollections.observableArrayList(result));
    }

    /**
     * Checks whether a loan matches the selected status filter.
     *
     * @param l loan to check
     * @param action selected filter
     * @return true if the loan matches the filter
     */
    private boolean matchAction(Loan l, String action) {

        if (STATUS_EN_COURS.equals(action))
            return l.getReturnDate() == null;

        if (STATUS_REMIS.equals(action))
            return l.getReturnDate() != null;

        return true;
    }

    /**
     * Checks whether a loan matches the selected date range.
     *
     * @param l loan to check
     * @param start start date
     * @param end end date
     * @return true if the loan is within the range
     */
    private boolean matchDate(Loan l, LocalDate start, LocalDate end) {

        if (start == null && end == null) return true;

        LocalDate d = l.getBorrowedDate().toLocalDate();

        if (start != null && d.isBefore(start)) return false;
        return end == null || !d.isAfter(end);
    }

    /**
     * Checks whether a loan matches the search keyword.
     *
     * @param l loan to check
     * @param keyword search keyword
     * @return true if the loan matches the search
     */
    private boolean matchSearch(Loan l, String keyword) {

        if (keyword == null || keyword.isBlank()) return true;

        String lower = keyword.toLowerCase();
        return l.getItemId().toLowerCase().contains(lower)
                || String.valueOf(l.getBorrowerId()).contains(lower)
                || String.valueOf(l.getId()).contains(lower);
    }
}
package be.esi.prj.service;

import be.esi.prj.model.Item;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.model.Loan;
import be.esi.prj.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for managing {@link Loan} operations.
 * <p>
 * Handles business rules related to borrowing, returning,
 * extending loans, and updating item availability.
 */
public class LoanService {

    private static final LoanService INSTANCE = new LoanService();
    private final ItemService itemService = ItemService.getInstance();
    private final LoanRepository loanRepository = new LoanRepositoryImpl();
    private final ObservableList<Loan> loans = FXCollections.observableArrayList();

    private LoanService() {}

    /**
     * Returns the singleton instance of the service.
     *
     * @return LoanService instance
     */
    public static LoanService getInstance() { return INSTANCE; }

    /**
     * Loads all loans from the repository into the observable list.
     *
     * @return observable list of loans
     */
    public ObservableList<Loan> load() {
        loans.setAll(getAllLoans());
        return loans;
    }

    /**
     * Retrieves all loans from the repository.
     *
     * @return list of all loans
     */
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    /**
     * Returns the observable list of loans.
     *
     * @return observable loans list
     */
    public ObservableList<Loan> getLoansObservable() {
        return loans;
    }

    /**
     * Finds a loan by its identifier.
     *
     * @param id loan identifier
     * @return the loan or null if not found
     */
    public Loan getLoanById(int id) {
        return loans.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves all loans for a given borrower.
     *
     * @param borrowerId borrower identifier
     * @return list of loans
     */
    public List<Loan> getLoansByBorrower(int borrowerId) {
        return loanRepository.findAll().stream().filter(l -> l.getBorrowerId() == borrowerId).toList();
    }

    /**
     * Creates a new loan and updates item availability.
     *
     * @param loan the loan to create
     * @throws IllegalStateException if the item is not available
     */
    public void createLoan(Loan loan) {

        Item item = itemService.getItemById(loan.getItemId());

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalStateException("Cet item est indisponible");
        }

        validate(loan);

        item.setStatus(ItemStatus.UNAVAILABLE);
        itemService.updateItem(item);

        loanRepository.insert(loan);
        loans.add(loan);
    }

    /**
     * Marks a loan as returned and updates item availability.
     *
     * @param id loan identifier
     * @return updated loan
     * @throws IllegalStateException if no active loan is found
     */
    public Loan returnLoan(int id) {

        Loan loan = getLoanById(id);

        if (loan == null || loan.getReturnDate() != null) {
            throw new IllegalStateException("Aucun prêt actif trouvé");
        }
        Item item = itemService.getItemById(loan.getItemId());
        loan.setReturnDate(LocalDateTime.now());
        item.setStatus(ItemStatus.AVAILABLE);

        loanRepository.update(loan);
        itemService.updateItem(item);

        int index = loans.indexOf(loan);
        if (index != -1) {
            loans.set(index, loan);
        }
        return loan;
    }

    /**
     * Extends the due date of a loan.
     *
     * @param id loan identifier
     */
    public void extendLoan(int id) {

        Loan loan = getLoanById(id);
        if (loan == null) throw new IllegalArgumentException("Loan not found");

        loan.extendDueDate();

        loanRepository.update(loan);

        int index = loans.indexOf(loan);
        if (index != -1) {
            loans.set(index, loan);
        }
    }

    // ================= STATS =================

    /**
     * Returns all active loans.
     *
     * @return list of active loans
     */
    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    /**
     * Returns all overdue loans.
     *
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    /**
     * Returns loans within a given date range.
     *
     * @param start start date
     * @param end end date
     * @return list of loans in range
     */
    public List<Loan> getLoansByDateRange(LocalDate start, LocalDate end) {
        return loanRepository.findLoansByDate(start, end);
    }

    /**
     * Counts active loans.
     *
     * @return number of active loans
     */
    public int countActiveLoans() {
        return getActiveLoans().size();
    }

    /**
     * Counts overdue loans.
     *
     * @return number of overdue loans
     */
    public int countOverdueLoans() {
        return getOverdueLoans().size();
    }

    /**
     * Counts loans created today.
     *
     * @return number of loans today
     */
    public int countLoansToday() {
        return loanRepository.findLoansByDate(LocalDate.now(), LocalDate.now()).size();
    }

    /**
     * Counts returns made today.
     *
     * @return number of returns today
     */
    public int countReturnsToday() {

        return (int) loans.stream()
                .filter(l -> l.getReturnDate() != null &&
                        l.getReturnDate().toLocalDate().equals(LocalDate.now()))
                .count();
    }

    // ================= END STATS =================

    /**
     * Validates a loan before creation.
     *
     * @param loan loan to validate
     * @throws IllegalArgumentException if required fields are missing
     */
    private void validate(Loan loan) {

        if (loan == null) throw new IllegalArgumentException("Loan cannot be null");

        if (loan.getBorrowerId() <= 0)
            throw new IllegalArgumentException("Borrower ID required");

        if (loan.getItemId() == null || loan.getItemId().isBlank())
            throw new IllegalArgumentException("Item ID required");
    }

}
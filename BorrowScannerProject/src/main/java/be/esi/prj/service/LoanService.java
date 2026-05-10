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

public class LoanService {

    private static final LoanService INSTANCE = new LoanService();
    public static LoanService getInstance() { return INSTANCE; }
    private final ItemService itemService = ItemService.getInstance();
    private final LoanRepository loanRepository = new LoanRepositoryImpl();
    private final ObservableList<Loan> loans = FXCollections.observableArrayList();


    private LoanService() {}

    // ================= LOAD
    public ObservableList<Loan> load() {
        loans.setAll(getAllLoans());
        return loans;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }


    // ================= GET
    public ObservableList<Loan> getLoansObservable() {
        return loans;
    }

    public Loan getLoanById(int id) {
        return loans.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Loan> getLoansByBorrower(int borrowerId) {
        return loanRepository.findAll().stream().filter(l -> l.getBorrowerId() == borrowerId).toList();
    }

    // ================= CREATE
    public void createLoan(Loan loan) {

        Item item = itemService.getItemById(loan.getItemId());

        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new IllegalStateException("Cet item est indisponible");
        }

        validate(loan);

        // 🔥 changer le statut ici (logique métier)
        item.setStatus(ItemStatus.UNAVAILABLE);
        itemService.updateItem(item);

        loanRepository.insert(loan);
        loans.add(loan);
    }

    // ================= RETURN
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

    // ================= EXTEND
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

    // ================= STATS (DB direct OK pour perf)
    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    public List<Loan> getLoansByDateRange(LocalDate start, LocalDate end) {
        return loanRepository.findLoansByDate(start, end);
    }

    public int countActiveLoans() {
        return getActiveLoans().size();
    }

    public int countOverdueLoans() {
        return getOverdueLoans().size();
    }

    public int countLoansToday() {
        return loanRepository.findLoansByDate(LocalDate.now(), LocalDate.now()).size();
    }

    public int countReturnsToday() {

        return (int) loans.stream()
                .filter(l -> l.getReturnDate() != null &&
                        l.getReturnDate().toLocalDate().equals(LocalDate.now()))
                .count();
    }

    // ================= VALIDATION
    private void validate(Loan loan) {

        if (loan == null) throw new IllegalArgumentException("Loan cannot be null");

        if (loan.getBorrowerId() <= 0)
            throw new IllegalArgumentException("Borrower ID required");

        if (loan.getItemId() == null || loan.getItemId().isBlank())
            throw new IllegalArgumentException("Item ID required");
    }

}
package be.esi.prj.service;

import be.esi.prj.model.Loan;
import be.esi.prj.repository.LoanRepository;
import be.esi.prj.repository.LoanRepositoryImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService() {
        this.loanRepository = new LoanRepositoryImpl();
    }

    // Return all loans
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // Find a loan by ID
    public Loan getLoanById(int id) {
        return loanRepository.findById(id);
    }

    // Active loans (not returned)
    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    // Overdue loans
    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    // Loans by borrower
    public List<Loan> getLoansByBorrower(int borrowerId) {
        return loanRepository.findByBorrowerId(borrowerId);
    }

    // Loans by item
    public List<Loan> getLoansByItem(String itemId) {
        return loanRepository.findByItemId(itemId);
    }

    // Loans between two dates
    public List<Loan> getLoansByDateRange(LocalDate start, LocalDate end) {
        return loanRepository.findLoansByDate(start, end);
    }

    // Register a new loan
    public void createLoan(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("Loan cannot be null");
        }
        if (loan.getBorrowerId() <= 0) {
            throw new IllegalArgumentException("Borrower ID is required");
        }
        if (loan.getItemId() == null || loan.getItemId().isBlank()) {
            throw new IllegalArgumentException("Item ID is required");
        }

        loanRepository.insert(loan);
    }
    // Mark a loan as returned
    public void returnLoan(int loanId) {
        Loan loan = loanRepository.findById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }
        loan.returnLoan();
        loanRepository.update(loan);
    }

    // Extend the due date of a loan
    public void extendLoan(int loanId) {
        Loan loan = loanRepository.findById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }
        loan.extendDueDate();
        loanRepository.update(loan);
    }

    // Number of active loans
    public int countActiveLoans() {
        return loanRepository.findActiveLoans().size();
    }

    // Number of overdue loans
    public int countOverdueLoans() {
        return loanRepository.findOverdueLoans().size();
    }

    // Number of loans today
    public int countLoansToday() {
        return loanRepository.findLoansByDate(LocalDate.now(), LocalDate.now()).size();
    }

    // Number of returns today
    public int countReturnsToday() {
        return (int) loanRepository.findAll().stream().filter(l -> l.getReturnDate() != null &&
                        l.getReturnDate().equals(LocalDateTime.now())).count();
    }
}
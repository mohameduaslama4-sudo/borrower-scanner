package be.esi.prj.repository;

import be.esi.prj.model.Loan;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository {
    List<Loan> findAll();
    Loan findById(int id);
    List<Loan> findByBorrowerId(int borrowerId);
    List<Loan> findRecentLoansByBorrower(int borrowerId);
    List<Loan> findByItemId(String itemId);
    List<Loan> findActiveLoans();
    List<Loan> findActiveLoansByBorrower(int borrowerId);
    List<Loan> findOverdueLoans();
    List<Loan> findLoansByDate(LocalDate start, LocalDate end);
    void insert(Loan loan);
    void update(Loan loan);
}
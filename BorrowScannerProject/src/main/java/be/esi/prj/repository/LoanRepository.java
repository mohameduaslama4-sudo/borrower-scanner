package be.esi.prj.repository;

import be.esi.prj.model.Loan;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository {
    List<Loan> findAll();
    Loan findById(int id);
    List<Loan> findByBorrowerId(int borrowerId);
    List<Loan> findByItemId(String itemId);
    List<Loan> findActiveLoans();
    List<Loan> findOverdueLoans();
    List<Loan> findLoansByDate(LocalDateTime start, LocalDateTime end);
    void insert(Loan loan);
    void update(Loan loan);
    void delete(int id);
}
package be.esi.prj.repository;

import be.esi.prj.model.Loan;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@link Loan} persistence.
 * <p>
 * Provides methods to retrieve and manage loans, including
 * active, overdue, and date-filtered loans.
 */
public interface LoanRepository {

    /**
     * Retrieves all loans from the data source.
     *
     * @return a list of all loans
     */
    List<Loan> findAll();

    /**
     * Retrieves all active loans (not yet returned).
     *
     * @return a list of active loans
     */
    List<Loan> findActiveLoans();

    /**
     * Retrieves all overdue loans.
     *
     * @return a list of overdue loans
     */
    List<Loan> findOverdueLoans();

    /**
     * Retrieves loans created between two dates.
     *
     * @param start start date (inclusive)
     * @param end end date (inclusive)
     * @return a list of loans in the given date range
     */
    List<Loan> findLoansByDate(LocalDate start, LocalDate end);

    /**
     * Inserts a new loan into the data source.
     *
     * @param loan the loan to insert
     */
    void insert(Loan loan);

    /**
     * Updates an existing loan in the data source.
     *
     * @param loan the loan with updated values
     */
    void update(Loan loan);
}
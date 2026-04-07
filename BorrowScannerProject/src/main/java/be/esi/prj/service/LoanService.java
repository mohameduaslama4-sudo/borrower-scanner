package be.esi.prj.service;

import be.esi.prj.model.Loan;
import be.esi.prj.repository.LoanRepository;
import be.esi.prj.repository.LoanRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService() {
        this(new LoanRepositoryImpl());
    }

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(int id) {
        return loanRepository.findById(id);
    }

    public List<Loan> getLoansByBorrowerId(int borrowerId) {
        return loanRepository.findByBorrowerId(borrowerId);
    }

    public List<Loan> getLoansByItemId(String itemId) {
        return loanRepository.findByItemId(itemId);
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    public List<Loan> getLoansBetween(LocalDateTime start, LocalDateTime end) {
        return loanRepository.findLoansByDate(start, end);
    }

    public Loan createLoan(String itemId, int borrowerId) {
        Loan loan = new Loan(itemId, borrowerId);
        loanRepository.insert(loan);
        return loan;
    }

    public void createLoan(Loan loan) {
        loanRepository.insert(loan);
    }

    public void updateLoan(Loan loan) {
        loanRepository.update(loan);
    }

    public void markReturned(Loan loan) {
        loan.markReturned();
        loanRepository.update(loan);
    }

    public void extendDueDate(Loan loan) {
        loan.extendDueDate();
        loanRepository.update(loan);
    }

    public void deleteLoan(int id) {
        loanRepository.delete(id);
    }
}

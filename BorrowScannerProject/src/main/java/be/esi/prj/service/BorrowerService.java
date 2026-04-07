package be.esi.prj.service;

import be.esi.prj.model.Borrower;
import be.esi.prj.repository.BorrowerRepository;
import be.esi.prj.repository.BorrowerRepositoryImpl;

import java.util.List;

public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerService() {
        this(new BorrowerRepositoryImpl());
    }

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    public Borrower getBorrowerById(int id) {
        return borrowerRepository.findById(id);
    }

    public List<Borrower> searchBorrowers(String keyword) {
        return borrowerRepository.findByNameOrSurname(keyword);
    }

    public void createBorrower(Borrower borrower) {
        borrowerRepository.insert(borrower);
    }

    public Borrower createBorrower(String firstName, String lastName, String email) {
        Borrower borrower = new Borrower(0, firstName, lastName, email);
        borrowerRepository.insert(borrower);
        return borrower;
    }

    public void updateBorrower(Borrower borrower) {
        borrowerRepository.update(borrower);
    }

    public void deleteBorrower(int id) {
        borrowerRepository.delete(id);
    }
}

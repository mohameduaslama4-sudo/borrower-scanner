package be.esi.prj.service;

import be.esi.prj.model.Borrower;
import be.esi.prj.repository.BorrowerRepository;
import be.esi.prj.repository.BorrowerRepositoryImpl;

import java.util.List;

public class BorrowerService {

    private final BorrowerRepository borrowerRepository;

    public BorrowerService() {
        this.borrowerRepository = new BorrowerRepositoryImpl();
    }

    // Return all borrowers
    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    // Return borrower by ID
    public Borrower getBorrowerById(int id) {
        return borrowerRepository.findById(id);
    }

    // Search by name or surname
    public List<Borrower> searchBorrowers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return borrowerRepository.findAll();
        }
        return borrowerRepository.findByNameOrSurname(keyword);
    }

    // Create a new borrower
    public void addBorrower(Borrower b) {
        if (b == null) {
            throw new IllegalArgumentException("Borrower cannot be null");
        }
        if (borrowerRepository.findById(b.getId()) != null) {
            throw new IllegalArgumentException("Borrower already exists");
        }
        if (b.getFirstName() == null || b.getFirstName().isBlank() ||
                b.getLastName() == null || b.getLastName().isBlank()) {
            throw new IllegalArgumentException("Missing required fields");
        }
        if (!isEmailValid(b.getEmail())) {
            throw new IllegalArgumentException("Invalid email");
        }
        borrowerRepository.insert(b);
    }

    // Update borrower email only
    public void updateBorrowerEmail(int borrowerId, String email) {
        Borrower borrower = borrowerRepository.findById(borrowerId);
        if (borrower == null) {
            throw new IllegalArgumentException("Borrower not found");
        }
        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        borrower.setEmail(email);
        borrowerRepository.update(borrower);
    }

    // Delete borrower
    public void deleteBorrower(int id) {
        if (borrowerRepository.findById(id) == null) {
            throw new IllegalArgumentException("Borrower not found");
        }
        borrowerRepository.delete(id);
    }

    // Email validation
    private boolean isEmailValid(String email) {
        return email != null && !email.isBlank() && email.contains("@") && email.contains(".");
    }
}
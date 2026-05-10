package be.esi.prj.service;

import be.esi.prj.model.Borrower;
import be.esi.prj.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class BorrowerService {

    private static final BorrowerService INSTANCE = new BorrowerService();
    private final BorrowerRepository borrowerRepository = new BorrowerRepositoryImpl();
    private final ObservableList<Borrower> borrowers = FXCollections.observableArrayList();

    public static BorrowerService getInstance() {
        return INSTANCE;
    }

    private BorrowerService() {}

    // ================= LOAD
    public ObservableList<Borrower> load() {
        borrowers.setAll(getAllBorrowers());
        return borrowers;
    }

    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    public ObservableList<Borrower> getBorrowersObservable() {
        return borrowers;
    }

    public Borrower getBorrowerById(int id) {
        return borrowers.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ================= CREATE
    public Borrower addBorrower(String last, String first, String email) {

        if (last == null || last.isBlank() || first == null || first.isBlank()
                || email == null || email.isBlank()) {
            throw new IllegalArgumentException("Veuillez remplir tous les champs");
        }
        if (!email.contains("@") || !email.contains(".")) throw new IllegalArgumentException("email invalide");
        Borrower b = new Borrower(last, first, email);
        int id = borrowerRepository.insert(b);
        b.setId(id);
        return b;
    }

    // ================= DELETE
    public void deleteBorrower(int id) {
        borrowerRepository.delete(id);
    }

    // ================= UPDATE EMAIL
    public void updateBorrowerEmail(int id, String email) {

        Borrower b = getBorrowerById(id);
        if (email == null || email.isBlank() || !email.contains("@") || !email.contains(".")) {
        throw new IllegalArgumentException("Email invalide");
        }
        b.setEmail(email.toUpperCase());
        borrowerRepository.updateEmail(b);

    }
}
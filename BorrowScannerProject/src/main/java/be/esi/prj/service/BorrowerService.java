package be.esi.prj.service;

import be.esi.prj.model.Borrower;
import be.esi.prj.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Service layer for managing {@link Borrower} operations.
 * <p>
 * Acts as an intermediary between the UI and the repository layer,
 * handling validation and business logic.
 */
public class BorrowerService {

    private static final BorrowerService INSTANCE = new BorrowerService();
    private final BorrowerRepository borrowerRepository = new BorrowerRepositoryImpl();
    private final ObservableList<Borrower> borrowers = FXCollections.observableArrayList();

    /**
     * Returns the singleton instance of the service.
     *
     * @return the BorrowerService instance
     */
    public static BorrowerService getInstance() {
        return INSTANCE;
    }

    private BorrowerService() {}

    /**
     * Loads all borrowers from the repository into the observable list.
     *
     * @return observable list of borrowers
     */
    public ObservableList<Borrower> load() {
        borrowers.setAll(getAllBorrowers());
        return borrowers;
    }

    /**
     * Retrieves all borrowers from the repository.
     *
     * @return list of all borrowers
     */
    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }

    /**
     * Returns the observable list of borrowers.
     *
     * @return observable borrowers list
     */
    public ObservableList<Borrower> getBorrowersObservable() {
        return borrowers;
    }

    /**
     * Finds a borrower by its identifier.
     *
     * @param id borrower identifier
     * @return the borrower or null if not found
     */
    public Borrower getBorrowerById(int id) {
        return borrowers.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates and persists a new borrower.
     *
     * @param last last name
     * @param first first name
     * @param email email address
     * @return the created borrower
     * @throws IllegalArgumentException if input data is invalid
     */
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

    /**
     * Deletes a borrower by its identifier.
     *
     * @param id borrower identifier
     */
    public void deleteBorrower(int id) {
        borrowerRepository.delete(id);
    }

    /**
     * Updates the email of an existing borrower.
     *
     * @param id borrower identifier
     * @param email new email address
     * @throws IllegalArgumentException if email is invalid
     */
    public void updateBorrowerEmail(int id, String email) {

        Borrower b = getBorrowerById(id);
        if (email == null || email.isBlank() || !email.contains("@") || !email.contains(".")) {
        throw new IllegalArgumentException("Email invalide");
        }
        b.setEmail(email.toUpperCase());
        borrowerRepository.updateEmail(b);

    }
}
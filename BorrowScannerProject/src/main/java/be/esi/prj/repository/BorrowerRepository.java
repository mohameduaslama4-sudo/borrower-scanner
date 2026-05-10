package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import java.util.List;

/**
 * Repository interface for managing {@link Borrower} persistence.
 * <p>
 * Provides basic CRUD operations for borrowers.
 */
public interface BorrowerRepository {

    /**
     * Retrieves all borrowers from the data source.
     *
     * @return a list of all borrowers
     */
    List<Borrower> findAll();

    /**
     * Inserts a new borrower into the data source.
     *
     * @param borrower the borrower to insert
     * @return the generated identifier of the inserted borrower
     */
    int insert(Borrower borrower);

    /**
     * Updates the email address of an existing borrower.
     *
     * @param borrower the borrower with updated email
     */
    void updateEmail(Borrower borrower);

    /**
     * Deletes a borrower by its identifier.
     *
     * @param id the borrower identifier
     */
    void delete(int id);
}
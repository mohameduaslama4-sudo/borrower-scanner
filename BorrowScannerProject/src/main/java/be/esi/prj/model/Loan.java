package be.esi.prj.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a loan of an item by a borrower.
 * <p>
 * A loan tracks the borrowing period, due date,
 * and optional return date of an item.
 */
public class Loan {

    private static int nextId = 1;
    private final int id;
    private final String itemId;
    private final int borrowerId;
    private final LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    /**
     * Creates a new loan when an item is borrowed.
     * <p>
     * The due date is automatically set to 30 days after the borrowing date.
     *
     * @param itemId the identifier of the borrowed item
     * @param borrowerId the identifier of the borrower
     */
    public Loan(String itemId, int borrowerId) {
        this.id = generateId();
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.dueDate = borrowedDate.plusDays(30);
        this.returnDate = null;
    }

    /**
     * Creates a loan loaded from the database.
     *
     * @param id the loan identifier
     * @param itemId the item identifier
     * @param borrowerId the borrower identifier
     * @param borrowedDate the borrowing date
     * @param dueDate the due date
     * @param returnDate the return date (can be null if not returned)
     */
    public Loan(int id, String itemId, int borrowerId, LocalDateTime borrowedDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        this.id = id;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    /**
     * Generates a unique loan identifier.
     *
     * @return a unique id
     */
    private static int generateId() {
        return nextId++;
    }

    /**
     * Returns the loan identifier.
     *
     * @return the loan id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the borrowed item identifier.
     *
     * @return the item id
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the borrower identifier.
     *
     * @return the borrower id
     */
    public int getBorrowerId() {
        return borrowerId;
    }

    /**
     * Returns the borrowing date.
     *
     * @return the borrowing date
     */
    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    /**
     * Returns the due date of the loan.
     *
     * @return the due date
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Returns the return date of the item, if returned.
     *
     * @return the return date or null if not returned
     */
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    /**
     * Extends the due date of the loan by 15 days.
     * <p>
     * A loan cannot be extended if it is already returned
     * or if the maximum extension limit is reached.
     *
     * @throws IllegalStateException if the loan cannot be extended
     */
    public void extendDueDate() {
        if (returnDate != null) {
            throw new IllegalStateException("Impossible de prolonger un prêt terminé");
        }
        if (dueDate.plusDays(15).isAfter(borrowedDate.plusDays(60))) {
            throw new IllegalStateException("Nombre maximum de prolongations atteint");
        }
        this.dueDate = dueDate.plusDays(15);
    }

    /**
     * Sets the return date of the loan.
     *
     * @param now the return timestamp
     * @throws IllegalStateException if the item has already been returned
     */
    public void setReturnDate(LocalDateTime now) {
        if (this.returnDate != null) {
            throw new IllegalStateException("Loan already returned");
        }
        this.returnDate = now;
    }
}
package be.esi.prj.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Loan {

    private static int nextId = 0;
    private final int id;
    private final String itemId;
    private final int borrowerId;
    private final LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    // Formatteur pour convertir le texte SQLite en objet Java LocalDateTime
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Constructeur pour le REPOSITORY (Chargement depuis DB avec Strings)
     */
    public Loan(int id, String itemId, int borrowerId, String borrowedDate, String returnDate) {
        this(id, itemId, borrowerId, borrowedDate, null, returnDate);
    }

    /**
     * Constructeur pour le REPOSITORY (Chargement depuis DB avec due_date)
     */
    public Loan(int id, String itemId, int borrowerId, String borrowedDate, String dueDate, String returnDate) {
        this.id = id;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = LocalDateTime.parse(borrowedDate, formatter);
        this.dueDate = (dueDate != null && !dueDate.isBlank())
                ? LocalDateTime.parse(dueDate, formatter)
                : this.borrowedDate.plusDays(30);
        this.returnDate = (returnDate != null) ? LocalDateTime.parse(returnDate, formatter) : null;
    }

    /**
     * Constructeur pour la CRÉATION (Nouveau prêt)
     */
    public Loan(String itemId, int borrowerId) {
        this.id = generateId();
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = LocalDateTime.now();
        this.dueDate = borrowedDate.plusDays(30);
        this.returnDate = null;
    }

    public int getId() { return id; }
    public String getItemId() { return itemId; }
    public int getBorrowerId() { return borrowerId; }
    public LocalDateTime getBorrowedDate() { return borrowedDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getReturnDate() { return returnDate; }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public void markReturned() {
        if (returnDate != null) throw new IllegalStateException("Prêt déjà terminé");
        this.returnDate = LocalDateTime.now();
    }

    public void extendDueDate() {
        if (returnDate != null) throw new IllegalStateException("Prêt terminé");
        this.dueDate = dueDate.plusDays(15);
    }

    private static int generateId() {
        if (nextId > 99999) throw new IllegalStateException("Maximum Loans atteint");
        return nextId++;
    }

    public String getFormattedId() {
        return String.format("%05d", id);
    }
}

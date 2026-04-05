package be.esi.prj.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Loan {

    private static int nextId = 0;
    private final int id;
    private final String itemId;
    private final int borrowerId;
    private final LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private final LocalDateTime returnDate;

    public Loan(String itemId, int borrowerId) {
        this.id = generateId();
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = LocalDateTime.now();
        this.dueDate = borrowedDate.plusDays(30);
        this.returnDate = null;
    }

    public int getId() {
        return id;
    }

    public String getItemId() {
        return itemId;
    }

    public int getBorrowerId() {
        return borrowerId;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void extendDueDate() {
        if (returnDate != null) {
            throw new IllegalStateException("Impossible de prolonger un prêt terminé");
        }
        if (dueDate.plusDays(15).isAfter(borrowedDate.plusDays(60))) {
            throw new IllegalStateException("Nombre maximum de prolongations atteint");
        }
        this.dueDate = dueDate.plusDays(15);
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    // Génère un ID unique pour Loan (0 à 99999)
    private static int generateId() {
        if (nextId > 99999) {
            throw new IllegalStateException("Maximum 99999 Loans atteint");
        }
        return nextId++;
    }

    public String getFormattedId() {
        // Retourne l'ID formaté à 5 chiffres
        return String.format("%05d", id);
    }
}
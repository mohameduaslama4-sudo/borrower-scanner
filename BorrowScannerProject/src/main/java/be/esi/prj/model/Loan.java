package be.esi.prj.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Loan {

    private static int nextId = 1;
    private final int id;
    private final String itemId;
    private final int borrowerId;
    private final LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;

    // Insertion en db
    public Loan(String itemId, int borrowerId) {
        this.id = generateId();
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        this.dueDate = borrowedDate.plusDays(30);
        this.returnDate = null;
    }

    // Lecture depuis la DB
    public Loan(int id, String itemId, int borrowerId, LocalDateTime borrowedDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        this.id = id;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    private static int generateId() {
        return nextId++;
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

    public void setReturnDate(LocalDateTime now) {
        if (this.returnDate != null) {
            throw new IllegalStateException("Loan already returned");
        }
        this.returnDate = now;
    }
}
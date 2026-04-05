package be.esi.prj.model;

public class Loan {

    private int id;
    private String itemId;
    private int borrowerId;
    private String borrowedDate;
    private String returnDate;

    public Loan(int id, String itemId, int borrowerId, String borrowedDate, String returnDate) {
        this.id = id;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.borrowedDate = borrowedDate;
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

    public String getBorrowedDate() {
        return borrowedDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
package be.esi.prj.model;

/**
 * Represents a borrower in the application.
 * <p>
 * A borrower can borrow items and is identified by
 * an automatically generated database identifier.
 */
public class Borrower {

    private int id;
    private final String firstName;
    private final String lastName;
    private String email;

    /**
     * Creates a borrower loaded from the database.
     *
     * @param id the borrower identifier
     * @param firstName the borrower's first name
     * @param lastName the borrower's last name
     * @param email the borrower's email address
     */
    public Borrower(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Creates a new borrower before database insertion.
     * <p>
     * The identifier is generated automatically by the database.
     *
     * @param firstName the borrower's first name
     * @param lastName the borrower's last name
     * @param email the borrower's email address
     */
    public Borrower(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Returns the borrower identifier.
     *
     * @return the borrower identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the borrower's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the borrower's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the borrower's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the borrower identifier.
     *
     * @param id the borrower identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Updates the borrower's email address.
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
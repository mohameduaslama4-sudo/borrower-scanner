package be.esi.prj.model;

public class Borrower {

    private static int nextId = 0;
    private final int id;
    private final String firstName;
    private final String lastName;
    private String email;

    /**
     * Constructeur pour le REPOSITORY (chargement depuis la DB)
     */
    public Borrower(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Constructeur pour la CRÉATION (nouvel utilisateur)
     */
    public Borrower(String firstName, String lastName, String email) {
        this(generateId(), firstName, lastName, email);
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    private static int generateId() {
        if (nextId > 999) {
            throw new IllegalStateException("Maximum 999 utilisateurs atteint");
        }
        return nextId++;
    }

    public String getFormattedId() {
        return String.format("%03d", id);
    }
}
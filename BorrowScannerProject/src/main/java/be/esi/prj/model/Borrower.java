package be.esi.prj.model;

public class Borrower {

    private static int nextId = 0;
    private final int id;
    private final String firstName;
    private final String lastName;
    private String email;

    public Borrower(String firstName, String lastName, String email) {
        this.id = generateId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Génère un ID unique pour Borrower (0 à 999)
    private static int generateId() {
        if (nextId > 999) { // limitation à 3 chiffres
            throw new IllegalStateException("Maximum 999 utilisateurs atteint");
        }
        return nextId++;
    }

    public String getFormattedId() {
        // Retourne l'ID formaté à 3 chiffres
        return String.format("%03d", id);
    }

}
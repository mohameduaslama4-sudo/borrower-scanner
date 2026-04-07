package be.esi.prj.model;

public class Item {

    private static int nextNumber = 1;
    private final String id;
    private final String name;
    private ItemCategory category;
    private ItemStatus status;
    private final String qrPath;

    /**
     * CONSTRUCTEUR 1 : Pour la base de données (5 paramètres)
     * On passe l'ID déjà existant.
     */
    public Item(String id, String name, ItemCategory category, ItemStatus status, String qrPath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
        this.qrPath = qrPath;
    }

    /**
     * CONSTRUCTEUR 2 : Pour la création initiale (4 paramètres)
     * L'ID est généré automatiquement.
     */
    public Item(String name, ItemCategory category, ItemStatus status, String qrPath) {
        this(generateId(category), name, category, status, qrPath);
    }

    // --- GETTERS & SETTERS ---

    public String getId() { return id; }
    public String getName() { return name; }
    public ItemCategory getCategory() { return category; }
    public ItemStatus getStatus() { return status; }
    public String getQrPath() { return qrPath; }

    public void setCategory(ItemCategory category) { this.category = category; }
    public void setStatus(ItemStatus status) { this.status = status; }

    // --- LOGIQUE ID ---

    private static String generateId(ItemCategory category) {
        String prefix = category.name().substring(0, 3).toUpperCase();
        String number = String.format("%03d", nextNumber++);
        return prefix + "-" + number;
    }
}
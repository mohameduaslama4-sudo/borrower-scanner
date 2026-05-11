package be.esi.prj.model;

/**
 * Represents an item that can be borrowed in the application.
 * <p>
 * Each item has a unique identifier, a category,
 * a status, and an associated QR code path.
 */
public class Item {

    private static int nextNumber = 1;
    private final String id;
    private final String name;
    private ItemCategory category;
    private ItemStatus status;
    private final String qrPath;

    /**
     * Creates a new item before database insertion.
     * <p>
     * The item identifier is generated automatically
     * according to its category.
     *
     * @param name the item name
     * @param category the item category
     * @param status the item status
     */
    public Item(String name, ItemCategory category, ItemStatus status) {
        this.id = generateId(category);
        this.name = name;
        this.category = category;
        this.status = status;
        this.qrPath = "external-data/qrcodes/" + id + ".png";
    }

    /**
     * Creates an item loaded from the database.
     *
     * @param id the item identifier
     * @param name the item name
     * @param category the item category
     * @param status the item status
     * @param qrPath the QR code file path
     */
    public Item(String id, String name, ItemCategory category, ItemStatus status, String qrPath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
        this.qrPath = qrPath;
    }

    /**
     * Returns the item identifier.
     *
     * @return the item identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the item name.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the item category.
     *
     * @return the item category
     */
    public ItemCategory getCategory() {
        return category;
    }

    /**
     * Returns the item status.
     *
     * @return the item status
     */
    public ItemStatus getStatus() {
        return status;
    }

    /**
     * Returns the QR code file path.
     *
     * @return the QR code path
     */
    public String getQrPath() {
        return qrPath;
    }

    /**
     * Updates the item category.
     *
     * @param category the new category
     */
    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    /**
     * Updates the item status.
     *
     * @param status the new status
     */
    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    /**
     * Generates a unique identifier for an item.
     * <p>
     * The identifier is composed of:
     * <ul>
     *     <li>the first three letters of the category</li>
     *     <li>an incremented numeric value</li>
     * </ul>
     *
     * @param category the item category
     * @return the generated identifier
     */
    private static String generateId(ItemCategory category) {
        String prefix = category.name().substring(0, 3).toUpperCase();
        String number = String.format("%03d", nextNumber++);
        return prefix + "-" + number;
    }
}

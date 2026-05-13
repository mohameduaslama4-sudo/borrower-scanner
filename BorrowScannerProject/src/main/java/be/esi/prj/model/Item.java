package be.esi.prj.model;

/**
 * Represents an item that can be borrowed in the application.
 * <p>
 * Each item has a unique identifier, a category,
 * a status, and an associated QR code path.
 */
public class Item {

    private final String id;
    private final String name;
    private ItemCategory category;
    private ItemStatus status;
    private final String qrPath;

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
     * Returns the 3-letter prefix of the item category.
     * <p>
     * The prefix is derived from the first three characters of the category name in uppercase.
     *
     * @param category the item category
     * @return the category prefix (e.g., "CAB", "RES")
     */
    public static String getPrefix(ItemCategory category) {
        return category.name().substring(0, 3).toUpperCase();

    }
}

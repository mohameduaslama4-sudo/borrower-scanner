package be.esi.prj.service;

import be.esi.prj.model.*;
import be.esi.prj.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Service layer for managing {@link Item} operations.
 * <p>
 * Handles business logic, validation, QR code generation,
 * and synchronization between repository and UI state.
 */
public class ItemService {

    private static final ItemService INSTANCE = new ItemService();
    private final QRService qrService =  new QRService();
    private final ItemRepository itemRepository = new ItemRepositoryImpl();
    private final ObservableList<Item> items = FXCollections.observableArrayList();

    private ItemService() {}

    /**
     * Returns the singleton instance of the service.
     *
     * @return ItemService instance
     */
    public static ItemService getInstance() { return INSTANCE; }

    /**
     * Loads all items from the repository into the observable list.
     *
     * @return observable list of items
     */
    public ObservableList<Item> load() {
        items.setAll(getAllItems());
        return items;
    }

    /**
     * Retrieves all items from the repository.
     *
     * @return list of items
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Returns the observable list of items.
     *
     * @return observable items list
     */
    public ObservableList<Item> getItemsObservable() {
        return items;
    }

    /**
     * Finds an item by its identifier.
     *
     * @param id item identifier
     * @return the item or null if not found
     */
    public Item getItemById(String id) {
        return items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a new item to the system.
     * <p>
     * Generates a unique ID based on category, persists the item in database,
     * updates the in-memory list, and generates the QR code.
     *
     * @param item the item to add (without ID)
     * @return the created and persisted item
     */
    public Item addItem(Item item) {
        validate(item);
        String prefix = Item.getPrefix(item.getCategory());
        String lastId = itemRepository.findLastItemIdByCategory(prefix);
        String newId = generateNextId(lastId, prefix);
        String qrPath = "external-data/qrcodes/" + newId + ".png";
        Item itemToAdd = new Item(newId, item.getName(), item.getCategory(), item.getStatus(), qrPath);
        itemRepository.insert(itemToAdd);
        items.add(itemToAdd);
        qrService.generateQRCode(itemToAdd.getId());
        return itemToAdd;
    }

    /**
     * Updates an existing item (category or status).
     *
     * @param item the item to update
     */
    public void updateItem(Item item) {

        itemRepository.update(item);
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
        }
    }

    /**
     * Deletes an item by its identifier.
     *
     * @param id item identifier
     */
    public void deleteItem(String id) {
        itemRepository.delete(id);
        items.removeIf(i -> i.getId().equals(id));
    }

    /**
     * Validates an item before persistence.
     *
     * @param item item to validate
     * @throws IllegalArgumentException if required fields are missing
     */
    private void validate(Item item) {

        if (item.getName() == null || item.getName().isBlank())
            throw new IllegalArgumentException("Name required");

        if (item.getCategory() == null)
            throw new IllegalArgumentException("Category required");
    }

    /**
     * Generates the next item ID based on the last stored ID.
     *
     * @param lastId the last ID stored in database (or null)
     * @param prefix the category prefix (e.g. "CAB")
     * @return the newly generated unique ID
     */
    private String generateNextId(String lastId, String prefix) {
        int next = 1;
        if (lastId != null) {
            String[] parts = lastId.split("-");
            next = Integer.parseInt(parts[1]) + 1;
        }
        return prefix + "-" + String.format("%03d", next);
    }
}


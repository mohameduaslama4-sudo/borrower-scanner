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
        List<Item> loadedItems = getAllItems();
        Item.synchronizeNextNumber(loadedItems);
        items.setAll(loadedItems);
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
     * Validates the item, persists it, updates the cache,
     * and generates its QR code.
     *
     * @param item the item to add
     * @return the created item
     */
    public Item addItem(Item item) {
        validate(item);
        Item.synchronizeNextNumber(itemRepository.findAll());
        Item itemToAdd = new Item(item.getName(), item.getCategory(), item.getStatus());
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
}

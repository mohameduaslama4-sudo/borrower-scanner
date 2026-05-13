package be.esi.prj.repository;

import be.esi.prj.model.Item;
import java.util.List;

/**
 * Repository interface for managing {@link Item} persistence.
 * <p>
 * Defines CRUD operations for items stored in the database.
 */
public interface ItemRepository {

    /**
     * Retrieves all items from the data source.
     *
     * @return a list of all items
     */
    List<Item> findAll();

    /**
     * Inserts a new item into the data source.
     *
     * @param item the item to insert
     */
    void insert(Item item);

    /**
     * Updates an existing item in the data source.
     *
     * @param item the item with updated values
     */
    void update(Item item);

    /**
     * Deletes an item by its identifier.
     *
     * @param id the item identifier
     */
    void delete(String id);

    /**
     * Finds the last item ID for a given category prefix.
     * <p>
     * The search is based on the item ID pattern (e.g., "CAB-001", "RES-002"),
     * and returns the highest existing ID matching the specified prefix.
     *
     * @param prefix the category prefix (e.g., "CAB", "RES")
     * @return the last item ID matching the prefix, or {@code null} if no item exists
     */
    String findLastItemIdByCategory(String prefix);
}


package be.esi.prj.service;

import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.repository.ItemRepository;
import be.esi.prj.repository.ItemRepositoryImpl;

import com.google.zxing.*;
import java.util.List;

public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService() {
        this.itemRepository = new ItemRepositoryImpl();
    }

    // Return all items
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Return item by ID
    public Item getItemById(String id) {
        return itemRepository.findById(id);
    }

    // Add an item to the inventory
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (itemRepository.findById(item.getId()) != null) {
            throw new IllegalArgumentException("Item already exists");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (item.getCategory() == null || item.getStatus() == null) {
            throw new IllegalArgumentException("Item category and status is required");
        }

        itemRepository.insert(item);
    }

    // Updates the category of an item
    public void updateItemCategorie(String itemId, ItemCategory category) {
        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found");
        }
        if (category == null) {
            throw new IllegalArgumentException("Item category is required");
        }
        item.setCategory(category);
        itemRepository.update(item);
    }

    // Updates the status of an item
    public void updateItemStatus(String itemId, ItemStatus status) {
        Item item = itemRepository.findById(itemId);

        if (item == null) {
            throw new IllegalArgumentException("Item not found");
        }

        if (status == null) {
            throw new IllegalArgumentException("Item status is required");
        }

        item.setStatus(status);
        itemRepository.update(item);
    }

    // Deletes an item from the inventory
    public void deleteItem(String id) {
        if (itemRepository.findById(id) == null) {
            throw new IllegalArgumentException("Item not found");
        }
        itemRepository.delete(id);
    }

    // Returns items by category
    public List<Item> getItemsByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category);
    }

    // Returns items by status
    public List<Item> getItemsByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    // Returns items by category and status
    public List<Item> getItemsByCategoryAndStatus(ItemCategory category, ItemStatus status) {
        return itemRepository.findByCategoryAndStatus(category, status);
    }

    // Searches for an item by name
    public List<Item> searchItemsByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return itemRepository.findAll();
        }
        return itemRepository.searchByName(keyword);
    }

    // QR Code generation for item
    public String generateQRCodeForItem(String itemId) {

        Item item = itemRepository.findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item not found");
        }
        QRService qrService = new QRService();
        return qrService.generateQRCode(itemId);
    }
}
package be.esi.prj.service;

import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import be.esi.prj.repository.ItemRepository;
import be.esi.prj.repository.ItemRepositoryImpl;

import java.util.List;

public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService() {
        this(new ItemRepositoryImpl());
    }

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(String id) {
        return itemRepository.findById(id);
    }

    public List<Item> searchByName(String keyword) {
        return itemRepository.searchByName(keyword);
    }

    public List<Item> getByCategory(ItemCategory category) {
        return itemRepository.findByCategory(category);
    }

    public List<Item> getByStatus(ItemStatus status) {
        return itemRepository.findByStatus(status);
    }

    public List<Item> getByCategoryAndStatus(ItemCategory category, ItemStatus status) {
        return itemRepository.findByCategoryAndStatus(category, status);
    }

    public Item createItem(String name, ItemCategory category, ItemStatus status, String qrPath) {
        Item item = new Item(name, category, status, qrPath);
        itemRepository.insert(item);
        return item;
    }

    public void createItem(Item item) {
        itemRepository.insert(item);
    }

    public void updateItem(Item item) {
        itemRepository.update(item);
    }

    public void deleteItem(String id) {
        itemRepository.delete(id);
    }
}

package be.esi.prj.service;

import be.esi.prj.model.*;
import be.esi.prj.repository.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ItemService {

    private static final ItemService INSTANCE = new ItemService();
    private final QRService qrService =  new QRService();
    private final ItemRepository itemRepository = new ItemRepositoryImpl();
    private final ObservableList<Item> items = FXCollections.observableArrayList();

    private ItemService() {}

    public static ItemService getInstance() { return INSTANCE; }

    // ================= LOAD
    public ObservableList<Item> load() {
        items.setAll(getAllItems());
        return items;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    // ================= GET
    public ObservableList<Item> getItemsObservable() {
        return items;
    }

    public Item getItemById(String id) {
        return items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ================= CREATE
    public Item addItem(Item item) {
        validate(item);
        itemRepository.insert(item);
        items.add(item);
        qrService.generateQRCode(item.getId());
        return item;
    }

    // ================= UPDATE CATEGORY OR STATUT
    public void updateItem(Item item) {

        itemRepository.update(item);
        int index = items.indexOf(item);
        if (index != -1) {
            items.set(index, item);
        }
    }

    // ================= DELETE
    public void deleteItem(String id) {
        itemRepository.delete(id);
        items.removeIf(i -> i.getId().equals(id));
    }

    // ================= VALIDATION
    private void validate(Item item) {

        if (item.getName() == null || item.getName().isBlank())
            throw new IllegalArgumentException("Name required");

        if (item.getCategory() == null)
            throw new IllegalArgumentException("Category required");
    }
}
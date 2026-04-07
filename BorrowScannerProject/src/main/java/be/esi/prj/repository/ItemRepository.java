package be.esi.prj.repository;

import be.esi.prj.model.Item;
import be.esi.prj.model.ItemCategory;
import be.esi.prj.model.ItemStatus;
import java.util.List;

public interface ItemRepository {
    List<Item> findAll();
    Item findById(String id);
    List<Item> searchByName(String keyword);
    List<Item> findByCategory(ItemCategory category);
    List<Item> findByStatus(ItemStatus status);
    List<Item> findByCategoryAndStatus(ItemCategory category, ItemStatus status);
    void insert(Item item);
    void update(Item item);
    void delete(String id);
}
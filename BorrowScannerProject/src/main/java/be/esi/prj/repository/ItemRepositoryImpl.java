package be.esi.prj.repository;

import be.esi.prj.model.*;
import be.esi.prj.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    private Item mapResultSet(ResultSet rs) {
        try {
            return new Item(
                    rs.getString("id"),
                    rs.getString("name"),
                    ItemCategory.valueOf(rs.getString("category")),
                    ItemStatus.valueOf(rs.getString("status")),
                    rs.getString("qr_path")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping Item", e);
        }
    }

    @Override
    public List<Item> findAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all items", e);
        }
        return list;
    }

    @Override
    public Item findById(String id) {
        String sql = "SELECT id, name, category, status, qr_path FROM items WHERE id = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding item by id", e);
        }
        return null;
    }

    @Override
    public List<Item> searchByName(String keyword) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items WHERE name LIKE ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Error searching items by name", e); }
        return list;
    }

    @Override
    public List<Item> findByCategory(ItemCategory c) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items WHERE category = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by category", e);
        }
        return list;
    }

    @Override
    public List<Item> findByStatus(ItemStatus s) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items WHERE status = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding items by status", e);
        }
        return list;
    }

    @Override
    public List<Item> findByCategoryAndStatus(ItemCategory c, ItemStatus s) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items WHERE category = ? AND status = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.name());
            pstmt.setString(2, s.name());
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapResultSet(rs)); }
        } catch (SQLException e) { throw new RuntimeException("Error finding items by category and status", e); }
        return list;
    }

    @Override
    public void insert(Item i) {
        String sql = "INSERT INTO items(id, name, category, status, qr_path) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, i.getId());
            pstmt.setString(2, i.getName());
            pstmt.setString(3, i.getCategory().name());
            pstmt.setString(4, i.getStatus().name());
            pstmt.setString(5, i.getQrPath());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting item", e);
        }
    }

    @Override
    public void update(Item i) {
        String sql = "UPDATE items SET category=?, status=? WHERE id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, i.getCategory().name());
            pstmt.setString(2, i.getStatus().name());
            pstmt.setString(3, i.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Error updating item", e); }
    }

    @Override public void delete(String id) {
        String sql = "DELETE FROM items WHERE id=?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting item", e);
        }
    }
}
package be.esi.prj.repository;

import be.esi.prj.model.*;
import be.esi.prj.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    private Item mapResultSet(ResultSet rs) throws SQLException {
        return new Item(
                rs.getString("id"),
                rs.getString("name"),
                ItemCategory.valueOf(rs.getString("category")),
                ItemStatus.valueOf(rs.getString("status")),
                rs.getString("qr_path")
        );
    }

    @Override
    public List<Item> findAll() {
        List<Item> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Item findById(String id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return mapResultSet(rs); }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Item> searchByName(String keyword) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE name LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override public List<Item> findByCategory(ItemCategory c) { return filter("category", c.name()); }
    @Override public List<Item> findByStatus(ItemStatus s) { return filter("status", s.name()); }

    private List<Item> filter(String column, String value) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE " + column + " = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Item> findByCategoryAndStatus(ItemCategory c, ItemStatus s) {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE category = ? AND status = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.name());
            pstmt.setString(2, s.name());
            try (ResultSet rs = pstmt.executeQuery()) { while (rs.next()) list.add(mapResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void insert(Item i) {
        String sql = "INSERT OR IGNORE INTO items(id, name, category, status, qr_path) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, i.getId());
            pstmt.setString(2, i.getName());
            pstmt.setString(3, i.getCategory().name());
            pstmt.setString(4, i.getStatus().name());
            pstmt.setString(5, i.getQrPath());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Item i) {
        String sql = "UPDATE items SET name=?, category=?, status=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, i.getName());
            pstmt.setString(2, i.getCategory().name());
            pstmt.setString(3, i.getStatus().name());
            pstmt.setString(4, i.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM items WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer l'item d'id " + id + ".", e);
        }
    }
}

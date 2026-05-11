package be.esi.prj.repository;

import be.esi.prj.model.*;
import be.esi.prj.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    // =========================
    // MAPPING
    // =========================
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

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Item> findAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, status, qr_path FROM items";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
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

    // =========================
    // INSERT
    // =========================
    @Override
    public void insert(Item i) {
        String sql = "INSERT INTO items(id, name, category, status, qr_path) VALUES(?,?,?,?,?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
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

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update(Item i) {
        String sql = "UPDATE items SET category=?, status=? WHERE id=?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, i.getCategory().name());
            pstmt.setString(2, i.getStatus().name());
            pstmt.setString(3, i.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Error updating item", e); }
    }

    // =========================
    // DELETE
    // =========================
    @Override public void delete(String id) {
        String sql = "DELETE FROM items WHERE id=?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting item", e);
        }
    }
}
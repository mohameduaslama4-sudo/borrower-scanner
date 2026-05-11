package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import be.esi.prj.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowerRepositoryImpl implements BorrowerRepository {

    // =========================
    // MAPPING
    // =========================
    private Borrower mapResultSet(ResultSet rs) {
        try {
            return new Borrower(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping Borrower", e);
        }
    }

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Borrower> findAll() {
        List<Borrower> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM borrowers";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all borrowers", e);
        }
        return users;
    }

    // =========================
    // INSERT
    // =========================
    @Override
    public int insert(Borrower b) {
        String sql = "INSERT INTO borrowers(first_name, last_name, email) VALUES(?,?,?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql,  Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, b.getFirstName());
            pstmt.setString(2, b.getLastName());
            pstmt.setString(3, b.getEmail());

            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // 🔥 ID généré
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting borrower", e);
        }
        return -1;
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void updateEmail(Borrower b) {
        String sql = "UPDATE borrowers SET email=? WHERE id=?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, b.getEmail());
            pstmt.setInt(2, b.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating borrower", e);
        }
    }

    // =========================
    // DELETE
    // =========================
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM borrowers WHERE id=?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting borrower", e);
        }
    }
}
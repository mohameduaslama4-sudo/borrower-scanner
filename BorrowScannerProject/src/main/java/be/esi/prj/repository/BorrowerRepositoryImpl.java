package be.esi.prj.repository;

import be.esi.prj.model.Borrower;
import be.esi.prj.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowerRepositoryImpl implements BorrowerRepository {

    @Override
    public List<Borrower> findAll() {
        List<Borrower> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM borrowers ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new Borrower(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les utilisateurs.", e);
        }
        return users;
    }

    @Override
    public Borrower findById(int id) {
        String sql = "SELECT id, first_name, last_name, email FROM borrowers WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Borrower(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger l'utilisateur d'id " + id + ".", e);
        }
        return null;
    }

    @Override
    public List<Borrower> findByNameOrSurname(String keyword) {
        List<Borrower> users = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM borrowers " +
                "WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new Borrower(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de rechercher des utilisateurs.", e);
        }
        return users;
    }

    @Override
    public void insert(Borrower b) {
        String sql = "INSERT INTO borrowers(first_name, last_name, email) VALUES(?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getFirstName());
            pstmt.setString(2, b.getLastName());
            pstmt.setString(3, b.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter l'utilisateur.", e);
        }
    }

    @Override
    public void update(Borrower b) {
        String sql = "UPDATE borrowers SET first_name=?, last_name=?, email=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getFirstName());
            pstmt.setString(2, b.getLastName());
            pstmt.setString(3, b.getEmail());
            pstmt.setInt(4, b.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de mettre à jour l'utilisateur.", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM borrowers WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer l'utilisateur d'id " + id + ".", e);
        }
    }
}

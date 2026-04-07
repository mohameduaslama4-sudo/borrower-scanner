package be.esi.prj.repository;

import be.esi.prj.model.Loan;
import be.esi.prj.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanRepositoryImpl implements LoanRepository {

    private Loan mapResultSet(ResultSet rs) throws SQLException {
        return new Loan(
                rs.getInt("id"),
                rs.getString("item_id"),
                rs.getInt("borrower_id"),
                rs.getString("borrowed_date"),
                rs.getString("due_date"),
                rs.getString("return_date")
        );
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY borrowed_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts.", e);
        }
        return list;
    }

    @Override
    public void insert(Loan l) {
        String sql = "INSERT INTO loans(item_id, borrower_id, borrowed_date, due_date, return_date) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getItemId());
            pstmt.setInt(2, l.getBorrowerId());
            pstmt.setString(3, l.getBorrowedDate().toString());
            pstmt.setString(4, l.getDueDate().toString());
            if (l.getReturnDate() == null) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, l.getReturnDate().toString());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter le prêt.", e);
        }
    }

    @Override
    public List<Loan> findActiveLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE return_date IS NULL ORDER BY borrowed_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts actifs.", e);
        }
        return list;
    }

    @Override
    public Loan findById(int id) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger le prêt d'id " + id + ".", e);
        }
        return null;
    }

    @Override
    public List<Loan> findByBorrowerId(int borrowerId) {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE borrower_id = ? ORDER BY borrowed_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts de l'utilisateur " + borrowerId + ".", e);
        }
        return list;
    }

    @Override
    public List<Loan> findByItemId(String itemId) {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE item_id = ? ORDER BY borrowed_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts de l'item " + itemId + ".", e);
        }
        return list;
    }

    @Override
    public List<Loan> findOverdueLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE return_date IS NULL AND due_date < ? ORDER BY due_date ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts en retard.", e);
        }
        return list;
    }

    @Override
    public List<Loan> findLoansByDate(LocalDateTime start, LocalDateTime end) {
        List<Loan> list = new ArrayList<>();
        if (start == null || end == null) {
            return list;
        }

        String sql = "SELECT * FROM loans WHERE borrowed_date BETWEEN ? AND ? ORDER BY borrowed_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, start.toString());
            pstmt.setString(2, end.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les prêts par période.", e);
        }
        return list;
    }

    @Override
    public void update(Loan l) {
        String sql = "UPDATE loans SET item_id=?, borrower_id=?, borrowed_date=?, due_date=?, return_date=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, l.getItemId());
            pstmt.setInt(2, l.getBorrowerId());
            pstmt.setString(3, l.getBorrowedDate().toString());
            pstmt.setString(4, l.getDueDate().toString());
            if (l.getReturnDate() == null) {
                pstmt.setNull(5, Types.VARCHAR);
            } else {
                pstmt.setString(5, l.getReturnDate().toString());
            }
            pstmt.setInt(6, l.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de mettre à jour le prêt d'id " + l.getId() + ".", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer le prêt d'id " + id + ".", e);
        }
    }
}

package be.esi.prj.repository;

import be.esi.prj.model.Loan;
import be.esi.prj.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanRepositoryImpl implements LoanRepository {

    // =========================
    // MAPPING
    // =========================
    private Loan mapResultSet(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String itemId = rs.getString("item_id");
            int borrowerId = rs.getInt("borrower_id");

            LocalDateTime borrowedDate = rs.getString("borrowed_date") != null
                    ? LocalDateTime.parse(rs.getString("borrowed_date"))
                    : null;

            LocalDateTime dueDate = rs.getString("due_date") != null
                    ? LocalDateTime.parse(rs.getString("due_date"))
                    : null;

            LocalDateTime returnDate = rs.getString("return_date") != null
                    ? LocalDateTime.parse(rs.getString("return_date"))
                    : null;

            return new Loan(id, itemId, borrowerId, borrowedDate, dueDate, returnDate);

        } catch (SQLException e) {
            throw new RuntimeException("Error mapping Loan", e);
        }
    }

    // =========================
    // FIND ALL
    // =========================
    @Override
    public List<Loan> findAll() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT id, item_id, borrower_id, borrowed_date, due_date, return_date FROM loans";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all loans", e);
        }

        return list;
    }

    // =========================
    // FIND ACTIVE LOANS
    // =========================
    @Override
    public List<Loan> findActiveLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT id, item_id, borrower_id, borrowed_date, due_date, return_date FROM loans WHERE return_date IS NULL";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding active loans", e);
        }

        return list;
    }

    // =========================
    // FIND OVERDUE LOANS
    // =========================
    @Override
    public List<Loan> findOverdueLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT id, item_id, borrower_id, borrowed_date, due_date, return_date FROM loans WHERE due_date < ? AND return_date IS NULL";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, LocalDateTime.now().toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding overdue loans", e);
        }

        return list;
    }


    // =========================
    // FIND LOANS BY DATE
    // =========================
    @Override
    public List<Loan> findLoansByDate(java.time.LocalDate start, java.time.LocalDate end) {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT id, item_id, borrower_id, borrowed_date, due_date, return_date FROM loans WHERE borrowed_date BETWEEN ? AND ?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, start.atStartOfDay().toString());
            pstmt.setString(2, end.atTime(23, 59).toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSet(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding loans by date range", e);
        }

        return list;
    }

    // =========================
    // INSERT
    // =========================
    @Override
    public void insert(Loan l) {
        String sql = "INSERT INTO loans(id, item_id, borrower_id, borrowed_date, due_date, return_date) VALUES(?,?,?,?,?,?)";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, l.getId());
            pstmt.setString(2, l.getItemId());
            pstmt.setInt(3, l.getBorrowerId());
            pstmt.setString(4, l.getBorrowedDate().toString());
            pstmt.setString(5, l.getDueDate().toString());
            pstmt.setString(6, l.getReturnDate() != null ? l.getReturnDate().toString() : null);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting loan", e);
        }
    }

    // =========================
    // UPDATE
    // =========================
    @Override
    public void update(Loan l) {
        String sql = "UPDATE loans SET due_date=?, return_date=? WHERE id=?";

        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, l.getDueDate().toString());
            pstmt.setString(2, l.getReturnDate() != null ? l.getReturnDate().toString() : null);
            pstmt.setInt(3, l.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating loan", e);
        }
    }
}